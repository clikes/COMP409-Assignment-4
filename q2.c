#include <stdio.h>
#include <stdlib.h>
#include <omp.h>
#include <string.h>
#include <time.h>
#include <sys/time.h>
/* Size of the DFA */
#define MAXSTATES 5
/* Number of characters in the alphabet */
#define ALPHABETSIZE 4
/* Size of the string to match against.  You may need to adjust this. */
#define STRINGSIZE 100000000

/* State transition table (ie the DFA) */
int stateTable[MAXSTATES][ALPHABETSIZE];

/* Initialize the table */
void initTable()
{
    int start = 0;
    int accept = 3;
    int reject = 4;

    /* Note that characters values are assumed to be 0-based. */
    stateTable[0][0] = 1;
    stateTable[0][1] = reject;
    stateTable[0][2] = reject;
    stateTable[0][3] = reject;

    stateTable[1][0] = 1;
    stateTable[1][1] = 2;
    stateTable[1][2] = reject;
    stateTable[1][3] = reject;

    stateTable[2][0] = reject;
    stateTable[2][1] = 2;
    stateTable[2][2] = 3;
    stateTable[2][3] = 3;

    stateTable[3][0] = 1;
    stateTable[3][1] = reject;
    stateTable[3][2] = 3;
    stateTable[3][3] = 3;

    // reject state
    stateTable[4][0] = reject;
    stateTable[4][1] = reject;
    stateTable[4][2] = reject;
    stateTable[4][3] = reject;
}

/* Construct a sample string to match against.  Note that this uses characters, encoded in ASCII,
   so to get 0-based characters you'd need to subtract 'a'. */
char *buildString()
{
    int i;
    char *s = (char *)malloc(sizeof(char) * (STRINGSIZE));
    if (s == NULL)
    {
        printf("\nOut of memory!\n");
        exit(1);
    }
    int max = STRINGSIZE - 3;

    /* seed the rnd generator (use a fixed number rather than the time for testing) */
    srand((unsigned int)time(NULL));

    /* And build a long string that might actually match */
    int j = 0;
    while (j < max)
    {
        s[j++] = 'a';
        while (rand() % 1000 < 997 && j < max)
            s[j++] = 'a';
        if (j < max)
            s[j++] = 'b';
        while (rand() % 1000 < 997 && j < max)
            s[j++] = 'b';
        if (j < max)
            s[j++] = (rand() % 2 == 1) ? 'c' : 'd';
        while (rand() % 1000 < 997 && j < max)
            s[j++] = (rand() % 2 == 1) ? 'c' : 'd';
    }
    s[max] = 'a';
    s[max + 1] = 'b';
    s[max + 2] = (rand() % 2 == 1) ? 'c' : 'd';
    s[max + 3] = '\0';
    return s;
}

int n = 0;
int (*dfaTable)[5];

void parallel(char *string){
    
    char *detect_position = string;
    int t = 1;
    int iteretion = (STRINGSIZE / (n + 1));
    int current_state = 0;
    int current_thread_num = omp_get_thread_num();
    int start_position = (STRINGSIZE / (n + 4)) * 4;
    if (current_thread_num != 0)
    {
        t = 4;
        current_state = 1;
        iteretion = STRINGSIZE / (n + 4);
        detect_position += (start_position + (iteretion)*(current_thread_num-1));
        }
    else
    {
        iteretion = (STRINGSIZE / (n + 4)) * 4;
    }
    if (current_thread_num == n){
        iteretion = STRINGSIZE - (start_position + (iteretion) * (current_thread_num - 1));
    } 
    int start_state = current_state;
    int result[5];
    //printf("iteretion: %d %d start position %d %d\n", iteretion, strlen(string), (detect_position- string ), omp_get_thread_num());
    for (int i = 0; i < t ; i++){
        current_state = start_state +i;
        
        for (int j = 0; j < iteretion; j++)
        {
            char current_char = detect_position[j];
            //printf("%d %c %d\n", current_thread_num,current_char,current_state);
            //puts("1");
            if (current_char == 'a'){
                current_state = stateTable[current_state][0];
            }
            else if (current_char == 'b'){
                current_state = stateTable[current_state][1];
            }
            else if (current_char == 'c' ){
                current_state = stateTable[current_state][2];

            }
            else if (current_char == 'd') {
                current_state = stateTable[current_state][3];
            } else {
                current_state = 4;
            }
        }
        
        dfaTable[current_thread_num][start_state+i] = current_state;
        //printf("current_thread_num: %d, start_state: %d, current_state: %d \n", current_thread_num, start_state+i, current_state);
    }
}

int main(int argc, char *argv[]) {
    
    if (argc > 1)
    {
        n = atoi(argv[1]);
        printf("Using %d threads\n", n);
    }
    initTable();
    omp_set_num_threads(n+1);
    int initTable[n+1][5];
    
    dfaTable = initTable;
    for (int j = 0; j < 5; j++)
    {
        dfaTable[0][j] = 0;
    }
    char *string = buildString();
    string[1] = 'f';

    struct timeval start;
    struct timeval end;
    gettimeofday(&start, NULL);
#pragma omp parallel
    parallel(string);
    
#pragma omp barrier
    int result_state = 0; //start state
    for (int i = 0 ; i< (n+1) ;i++){
        result_state = dfaTable[i][result_state];
        printf("%d %d\n", i,result_state);
        // for (int j = 0; j < 5; j++)
        // {
        //     printf("Table: %d %d \n", j, dfaTable[i][j]);
        // }
    }

    gettimeofday(&end, NULL);

    printf("%d time: %ld\n", result_state, (1000000 * (end.tv_sec - start.tv_sec ))+ end.tv_usec - start.tv_usec);
    return 0;
}