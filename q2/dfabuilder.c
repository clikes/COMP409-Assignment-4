#include <stdlib.h>
#include <stdio.h>

/* Size of the DFA */
#define MAXSTATES 5
/* Number of characters in the alphabet */
#define ALPHABETSIZE 4
/* Size of the string to match against.  You may need to adjust this. */
#define STRINGSIZE 100000000

/* State transition table (ie the DFA) */
int stateTable[MAXSTATES][ALPHABETSIZE];

/* Initialize the table */
void initTable() {
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
char *buildString() {
    int i;
    char *s = (char *)malloc(sizeof(char)*(STRINGSIZE));
    if (s==NULL) {
        printf("\nOut of memory!\n");
        exit(1);
    }
    int max = STRINGSIZE-3;

    /* seed the rnd generator (use a fixed number rather than the time for testing) */
    srand((unsigned int)time(NULL)); 

    /* And build a long string that might actually match */
    int j=0;
    while(j<max) {
        s[j++] = 'a';
        while (rand()%1000<997 && j<max) 
            s[j++] = 'a';
        if (j<max)
            s[j++] = 'b';
        while (rand()%1000<997 && j<max) 
            s[j++] = 'b';
        if (j<max)
            s[j++] = (rand()%2==1) ? 'c' : 'd';
        while (rand()%1000<997 && j<max) 
            s[j++] = (rand()%2==1) ? 'c' : 'd';
    }
    s[max] = 'a';
    s[max+1] = 'b';
    s[max+2] = (rand()%2==1) ? 'c' : 'd';
    return s;
}

// int main(int argc, char *argv[]) {
//     initTable();
//     char *string = buildString();
//     printf("Built: [0]=%c and [%d]=%c\n", string[0], (int)(STRINGSIZE - 1), string[STRINGSIZE - 1]);
//     return 0;
// }


