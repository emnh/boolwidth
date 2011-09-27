#include <stdio.h>
#include <stdlib.h>
#include <glib.h>
#include <gc.h>

typedef guint32 bitset;

guint g_hash(gconstpointer v) {
    return *((bitset*) v);
}

gboolean g_equal(gconstpointer v1, gconstpointer v2) {
    return *((bitset*) v1) == *((bitset*) v2);
}

int countNeighbourHoods(int neighbourHoodCount, bitset* neighbourHoods) {

    bitset* generations[neighbourHoodCount];
    int generationCounts[neighbourHoodCount];
    int in_set = 1;
    int count = 0;
    GHashTable* table = g_hash_table_new(g_int_hash, g_int_equal);

    generations[0] = calloc(1, sizeof(bitset));
    bitset first_set = 0;
    generations[0][0] = first_set; // generation of empty set
    g_hash_table_insert(table, &first_set, &in_set);
    generationCounts[0] = 1;
    count++;

    // for each neighborhood to add
    for (int i = 0; i < neighbourHoodCount; i++) {
        int j = i + 1;
        generations[j] = calloc(count, sizeof(bitset));
        generationCounts[j] = 0;
        // for each old generation k
        for (int k = 0; k < j; k++) {
            // for each element l in old generation
            for (int l = 0; l < generationCounts[k]; l++) {
                bitset new_set = generations[k][l] | neighbourHoods[i];
                gpointer elem = g_hash_table_lookup(table, &new_set);
                char* about = "OLD";
                if (elem == NULL) {
                    about = "NEW";
                    generations[j][generationCounts[j]] = new_set;
                    g_hash_table_insert(
                            table,
                            &generations[j][generationCounts[j]],
                            &in_set);
                    generationCounts[j]++;
                    count++;
                }
                /*
                printf("%s: generation %d element %d + neighborhood %d(=%d) = new_set: %d\n", 
                        about, k, l, i, neighbourHoods[i], new_set);
                        */
            }
        }
    }

    for (int i = 0; i < neighbourHoodCount; i++) {
        free(generations[i]);
    }

    //printf("hash size: %d\n", g_hash_table_size(table));

    g_hash_table_destroy(table);

    return count;

    //free(generations);
}


bitset find_first_one(bitset set) {
    for (bitset onebit = 1 << (sizeof(bitset) * 8 - 1);
            onebit > 0;
            onebit >>= 1) {
        if ((set & onebit) != 0) {
            return onebit;
        }
    }
    puts("find_first_one: no bit set");
    exit(1);
}

bitset* filter_bitsets(bitset* sets, bitset filter) {
    return NULL;
}

int countNeighbourHoodsBranch(int neighbourHoodCount, bitset* neighbourHoods) {
    int branch(bitset in_set, bitset out_set, bitset rest, int still_valid_count, bitset* still_valid_neighborhoods) {

        bitset selected = find_first_one(rest);
        int count = 0;
        
        // rest without selected
        bitset new_rest = rest & ~selected;

        // in branch
        bitset* in_sets_valid;
        count += branch(in_set & selected, out_set, rest, 0, in_sets_valid);

        // out branch
        bitset* out_sets_valid;
        count += branch(in_set, out_set & selected, rest, 0, out_sets_valid);

        return count;
    }
    return branch(0, 0, 0, 0, NULL);
}

char* bin_string(bitset a) {
    int len = sizeof(bitset) * 8;
    char* out = GC_MALLOC((len + 1) * sizeof(char));
    char* end = out + len;
    //while (a > 0) {
    while (end >= out) {
        *(end--) = (a % 2 == 0) ? '0' : '1';
        a /= 2;
    }
    return out;
}

int main (int argc, char* argv[]) {

    bitset neighbourHoods[] = {
        0b1,
        0b11,
        0b101,
        0b1001,
        0b10001,
        0b100001,
        0b1000001
    };
    int neighbourHoodCount = sizeof(neighbourHoods) / sizeof(bitset);

    const int neighbourHoodCount2 = 24; 
    bitset neighbourHoods2[neighbourHoodCount2];
    for (int i = 0; i < neighbourHoodCount2; i++) {
        neighbourHoods2[i] = 1 << i;
    }
    printf("bin: %s\n", bin_string(0b1011001));
    printf("ff: %s\n", bin_string(find_first_one(0b111111)));

    printf("%d\n", countNeighbourHoods(neighbourHoodCount2, neighbourHoods2));
    //printf("%d\n", countNeighbourHoodsBranch(neighbourHoodCount2, neighbourHoods2));

    exit(0);
}

