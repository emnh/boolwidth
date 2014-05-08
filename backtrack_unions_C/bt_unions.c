#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define DEBUG false

struct union_state {
  int rowCount;
  int colCount;
  int** mat;
  int* sample;
  float prob;
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

float unions_sample(struct union_state* state) {
  int* qpos = malloc(sizeof(int) * state->colCount);
  int qcount = 0;
  float prob;
  for (int i = 0; i < state->colCount; i++) {
    if (state->sample[i] == QVAL) {
      qpos[qcount] = i;
      qcount++;
    }
  }
  if (qcount == 0) {
    prob = state->prob;
  } else {
    int newposrand = rand() % qcount;
    int newposition = qpos[newposrand];
    int* newsample0 = malloc(sizeof(int) * state->colCount);
    int* newsample1 = malloc(sizeof(int) * state->colCount);
    newsample0[newposition] = 0;
    newsample1[newposition] = 1;
    int val0 = isPartialHood(state, newsample0);
    int val1 = isPartialHood(state, newsample1);
    struct union_state newstate;
    int randval;
    switch (val0 + val1) {
      case 0:
        prob = state->prob;
        break;
      case 1:
        memcpy(&newstate, state, sizeof(newstate));
        if (val0) {
          newstate.sample = newsample0;
        } else if (val1) {
          newstate.sample = newsample1;
        }
        prob = unions_sample(&newstate);
      case 2:
        memcpy(&newstate, state, sizeof(newstate));
        randval = rand() % 2;
        if (randval == 0) {
          newstate.sample = newsample0;
        } else if (val1) {
          newstate.sample = newsample1;
        }
        prob = unions_sample(&newstate);
    }
    free(newsample0);
    free(newsample1);
  }
  free(qpos);
  return prob;
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
    if (DEBUG) printf("val0: %d\n", val0);
    int val1 = isPartialHood(state, newsample1);
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
    //int max = 18;
    state.colCount = 8;
    state.rowCount = 8;
    int mat[5][5] = {
      {0, 0, 0, 0, 1},
      {0, 0, 0, 1, 0},
      {0, 0, 1, 0, 0},
      {0, 1, 0, 0, 0},
      {1, 0, 0, 0, 0}
    };
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
        if (i == j) {
          state.mat[i][j] = 1;
        } else {
          state.mat[i][j] = 0;
        }
        //state.mat[i][j] = mat[i][j];
      }
    }

    int hoodcount = unions_iterate(&state);
    printf("%d\n", hoodcount);

    // free mat
    for (int i = 0; i < state.rowCount; i++) {
      free(state.mat[i]);
    }
    free(state.mat);
    free(state.sample);

    exit(0);
}

