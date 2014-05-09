#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <time.h>
#include <math.h>

#define DEBUG false

struct union_state {
  int rowCount;
  int colCount;
  int** mat;
  int* sample;
  int estimate;
  int position;
};

const int QVAL = -1;

bool isPartialHood(struct union_state* state, int* sample) {
  int** subsetRows = malloc(state->rowCount*sizeof(int*));
  int subsetRowCount = 0;
  for (int rowIndex = 0; rowIndex < state->rowCount; rowIndex++) {
    bool allColsOK = true;
    for (int colIndex = 0; colIndex < state->colCount; colIndex++) {
      allColsOK = allColsOK && (sample[colIndex] == QVAL || state->mat[rowIndex][colIndex] <= sample[colIndex]);
    }
    if (allColsOK) {
      subsetRows[subsetRowCount++] = state->mat[rowIndex];
    }
  }
  int* subsetUnion = malloc(sizeof(int) * state->colCount);
  for (int colIndex = 0; colIndex < state->colCount; colIndex++) {
    subsetUnion[colIndex] = 0;
  }
  for (int rowIndex = 0; rowIndex < subsetRowCount; rowIndex++) {
    for (int colIndex = 0; colIndex < state->colCount; colIndex++) {
      subsetUnion[colIndex] = subsetUnion[colIndex] | subsetRows[rowIndex][colIndex];
    }
  }
  bool subsetUnionEqualsSample = true;

  if (DEBUG) {
    printf("sample: "); 
    for (int colIndex = 0; colIndex < state->colCount; colIndex++) {
      printf("%d,", sample[colIndex]);
    }
    printf("\n");
  }

  if (DEBUG) printf("subset union (rows=%d) ", subsetRowCount); 
  for (int colIndex = 0; colIndex < state->colCount; colIndex++) {
    if (DEBUG) printf("%d,", subsetUnion[colIndex]);
    subsetUnionEqualsSample = subsetUnionEqualsSample && (sample[colIndex] == QVAL || subsetUnion[colIndex] == sample[colIndex]);
  }
  if (DEBUG) printf("\n");
  free(subsetRows);
  free(subsetUnion);
  return subsetUnionEqualsSample; 
}

long unions_sample(struct union_state* state) {
  int* qpos = malloc(sizeof(int) * state->colCount);
  int qcount = 0;
  long estimate;
  for (int i = 0; i < state->colCount; i++) {
    if (state->sample[i] == QVAL) {
      qpos[qcount] = i;
      qcount++;
    }
  }
  if (DEBUG) printf("qcount: %d\n", qcount);
  if (qcount == 0) {
    estimate = 1; //state->estimate;
  } else {
    int newposrand = rand() % qcount;
    int newposition = qpos[newposrand];
    int* newsample0 = malloc(sizeof(int) * state->colCount);
    int* newsample1 = malloc(sizeof(int) * state->colCount);
    memcpy(newsample0, state->sample, state->colCount * sizeof(int));
    memcpy(newsample1, state->sample, state->colCount * sizeof(int));
    newsample0[newposition] = 0;
    newsample1[newposition] = 1;
    int val0 = isPartialHood(state, newsample0);
    int val1 = isPartialHood(state, newsample1);
    if (DEBUG) printf("val0: %d\n", val0);
    if (DEBUG) printf("val1: %d\n", val1);
    struct union_state* newstate;
    int randval;
    switch (val0 + val1) {
      case 0:
        estimate = 1; //state->estimate;
        break;
      case 1:
        newstate = malloc(sizeof(struct union_state));
        memcpy(newstate, state, sizeof(struct union_state));
        if (val0) {
          newstate->sample = newsample0;
        } else if (val1) {
          newstate->sample = newsample1;
        }
        estimate = unions_sample(newstate);
        break;
      case 2:
        newstate = malloc(sizeof(struct union_state));
        memcpy(newstate, state, sizeof(struct union_state));
        randval = rand() % 2;
        if (randval == 0) {
          newstate->sample = newsample0;
        } else if (val1) {
          newstate->sample = newsample1;
        }
        estimate = unions_sample(newstate) * 2;
        break;
    }
    free(newsample0);
    free(newsample1);
  }
  free(qpos);
  return estimate;
}

int unions_iterate(struct union_state* state) {
  int hoodcount = 0;
  if (state->position >= state->colCount) {
    return 1;
  } else {
    int position = state->position;
    //printf("position: %d\n", position);
    int* newsample0 = malloc(sizeof(int) * state->colCount);
    int* newsample1 = malloc(sizeof(int) * state->colCount);
    memcpy(newsample0, state->sample, state->colCount * sizeof(int));
    memcpy(newsample1, state->sample, state->colCount * sizeof(int));
    newsample0[position] = 0;
    newsample1[position] = 1;
    int val0 = isPartialHood(state, newsample0);
    int val1 = isPartialHood(state, newsample1);
    if (DEBUG) printf("val0: %d\n", val0);
    if (DEBUG) printf("val1: %d\n", val1);
    struct union_state* newstate;
    int randval;
    switch (val0 + val1) {
      case 0:
        break;
      case 1:
        newstate = malloc(sizeof(struct union_state));
        memcpy(newstate, state, sizeof(struct union_state));
        if (val0) {
          newstate->sample = newsample0;
          newstate->position = position + 1;
        } else if (val1) {
          newstate->sample = newsample1;
          newstate->position = position + 1;
        }
        hoodcount = unions_iterate(newstate);
        free(newstate);
        break;
      case 2:
        newstate = malloc(sizeof(struct union_state));
        memcpy(newstate, state, sizeof(struct union_state));
        newstate->sample = newsample0;
        newstate->position = position + 1;
        hoodcount += unions_iterate(newstate);
        newstate->sample = newsample1;
        newstate->position = position + 1;
        hoodcount += unions_iterate(newstate);
        free(newstate);
        break;
    }
    free(newsample0);
    free(newsample1);
  }
  return hoodcount;
}

int main (int argc, char* argv[]) {
    struct union_state state;

    scanf("%d", &state.colCount);
    scanf("%d", &state.rowCount);

    state.position = 0;
    state.sample = malloc(state.colCount * sizeof(int));

    // init sample
    for (int i = 0; i < state.colCount; i++) {
      state.sample[i] = QVAL;
    }

    // init mat
    state.mat = malloc((state.rowCount + 1) * sizeof(int*));
    for (int i = 0; i < state.rowCount; i++) {
      state.mat[i] = malloc(state.colCount * sizeof(int));
      for (int j = 0; j < state.colCount; j++) {
        scanf("%d", &state.mat[i][j]);
      }
    }

    clock_t cstart;
    clock_t cend;
    long elapsed;

    int samplect = 100;
    long sum = 0;
    long sample = 0;
    cstart = clock();
    for (int i = 0; i < samplect; i++) {
      sample = unions_sample(&state);
      //printf("sample: %d\n", sample);
      sum += sample;
    }
    cend = clock();
    elapsed = ((long) cend - (long) cstart) / 1000;
    long estimate = sum / samplect;
    printf("sampled (%ldms): %ld, log2 sampled: %.2f\n", elapsed, estimate, log2(estimate));

    if (estimate < 100000) {
      cstart = clock();
      int hoodcount = unions_iterate(&state);
      cend = clock();
      elapsed = ((long) cend - (long) cstart) / 1000;
      printf("exact (%ldms): %d, log2 exact: %.2f\n", elapsed, hoodcount, log2(hoodcount));
      double accuracy = (double) estimate / (double) hoodcount;
      printf("accuracy: %.3f, accuracy log2: %.3f\n",
          accuracy,
          log2(estimate) / log2(hoodcount));
    }

    // free mat
    for (int i = 0; i < state.rowCount; i++) {
      free(state.mat[i]);
    }
    free(state.mat);
    free(state.sample);

    exit(0);
}

