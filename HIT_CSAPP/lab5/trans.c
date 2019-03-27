/* 
 * trans.c - Matrix transpose B = A^T
 *
 * Each transpose function must have a prototype of the form:
 * void trans(int M, int N, int A[N][M], int B[M][N]);
 *
 * A transpose function is evaluated by counting the number of misses
 * on a 1KB direct mapped cache with a block size of 32 bytes.
 */ 
#include <stdio.h>
#include "cachelab.h"

int is_transpose(int M, int N, int A[N][M], int B[M][N]);

/* 
 * transpose_submit - This is the solution transpose function that you
 *     will be graded on for Part B of the assignment. Do not change
 *     the description string "Transpose submission", as the driver
 *     searches for that string to identify the transpose function to
 *     be graded. 
 */
char transpose_submit_desc[] = "Transpose submission";
void transpose_submit(int M, int N, int A[N][M], int B[M][N])
{
    /* 32 * 32 */
    if(M == 32)
    {
        int i, j, m, n, tmp;
        for(i = 0; i < M; i+=8)
            for(j = 0; j < N; j+=8)
            {
                if(i == j)
                    tmp = A[i][j];
                for(m = i; m < i + 8; ++m)
                {
                    for(n = j; n < j + 8; ++n)
                    {
                        if(m == n)
                        {
                            B[m][m] = tmp;
                            tmp = A[m+1][m+1];
                        }
                        else
                            B[m][n] = A[n][m];
                    }
                }
            }
    }
    /* 64 * 64 */
    else if(M == 64 && N == 64)
    {
        int i, j, m;
        int tmp1, tmp2, tmp3, tmp4, tmp5, tmp6, tmp7, tmp8;
            for(i = 0; i < N ;i += 8) 
            {
                for(j = 0; j < M; j += 8) 
                {
                    for(m = j; m < j + 4; ++m) 
                    {
                        tmp1 = A[m][i+0];
                        tmp2 = A[m][i+1];
                        tmp3 = A[m][i+2];
                        tmp4 = A[m][i+3];
                        tmp5 = A[m][i+4];
                        tmp6 = A[m][i+5];
                        tmp7 = A[m][i+6];
                        tmp8 = A[m][i+7];
                        B[i+0][m] = tmp1;
                        B[i+1][m] = tmp2;
                        B[i+2][m] = tmp3;
                        B[i+3][m] = tmp4;
                        B[i+0][m+4] = tmp5;
                        B[i+1][m+4] = tmp6;
                        B[i+2][m+4] = tmp7;
                        B[i+3][m+4] = tmp8;
                    }
                    for(m = i + 4; m < i + 8; ++m) 
                    {
                        tmp1 = B[m-4][j+4];
                        tmp2 = B[m-4][j+5];
                        tmp3 = B[m-4][j+6];
                        tmp4 = B[m-4][j+7];
                        B[m-4][j+4] = A[j+4][m-4];
                        B[m-4][j+5] = A[j+5][m-4];
                        B[m-4][j+6] = A[j+6][m-4];
                        B[m-4][j+7] = A[j+7][m-4];
                        B[m][j+0] = tmp1;
                        B[m][j+1] = tmp2;
                        B[m][j+2] = tmp3;
                        B[m][j+3] = tmp4;
                        B[m][j+4] = A[j+4][m];
                        B[m][j+5] = A[j+5][m];
                        B[m][j+6] = A[j+6][m];
                        B[m][j+7] = A[j+7][m];
                    }
                }
            }

    }
    /* 61 * 67 */
    else
    {
        int i, j, m, n;
        for(i = 0; i < N; i+=12)
            for(j = 0; j < M; j+=12)
                for(m = j; m < j + 12 && m < M; ++m)
                    for(n = i; n < i + 12 && n < N; ++n)
                        B[m][n] = A[n][m];                 
    }
}

char simple_trans_desc[] = "Simple Transpose";
void simple_trans(int M, int N, int A[N][M], int B[M][N])
{
    for(int i = 0; i < N; ++i)
        for(int j = 0; j < M; ++j)
            B[i][j] = A[j][i];
}

void transfor32(int M, int N, int A[N][M], int B[M][N])
{
    int i, j, m, n, tmp;
    for(i = 0; i < M; i+=8)
        for(j = 0; j < N; j+=8)
        {
            if(i == j)
                tmp = A[i][j];
            for(m = i; m < i + 8; ++m)
            {
                if(i == j)
                    B[j][i] = tmp;
                for(n = j; n < j + 8; ++n)
                {
                    if(m == n)
                        tmp = A[m+1][n+1];
                    else
                        B[m][n] = A[n][m];
                }
            }
        }
}

/* 
 * You can define additional transpose functions below. We've defined
 * a simple one below to help you get started. 
 */ 

/* 
 * trans - A simple baseline transpose function, not optimized for the cache.
 */
char trans_desc[] = "Simple row-wise scan transpose";
void trans(int M, int N, int A[N][M], int B[M][N])
{
    int i, j;
    int n1, n2, n3, n4, n5, n6, n7, n8;

    for(i = 0; i < M; i+=2)
    {
        for(j = 0; j < N; j+=4)
        {
            n1 = A[i][j+0];
            n2 = A[i][j+1];
            n3 = A[i][j+2];
            n4 = A[i][j+3];
            n5 = A[i+1][j+0];
            n6 = A[i+1][j+1];
            n7 = A[i+1][j+2];
            n8 = A[i+1][j+3];
            B[j+0][i] = n1;
            B[j+0][i+1] = n5;
            B[j+1][i] = n2;
            B[j+1][i+1] = n6;
            B[j+2][i] = n3;
            B[j+2][i+1] = n7;
            B[j+3][i] = n4;
            B[j+3][i+1] = n8;
        }
        if(j == N)
            continue;
        for(j = N - 4; j < N; ++j)
        {
            n1 = A[i][j];
            B[j][i] = n1;
        }
    }
    if(i != M)
    {
        i = M-1;
        for(j = 0; j < N; j+=4)
        {
            n1 = A[i][j+0];
            n2 = A[i][j+1];
            n3 = A[i][j+2];
            n4 = A[i][j+3];
            n5 = A[i+1][j+0];
            n6 = A[i+1][j+1];
            n7 = A[i+1][j+2];
            n8 = A[i+1][j+3];
            B[j+0][i] = n1;
            B[j+0][i+1] = n5;
            B[j+1][i] = n2;
            B[j+1][i+1] = n6;
            B[j+2][i] = n3;
            B[j+2][i+1] = n7;
            B[j+3][i] = n4;
            B[j+3][i+1] = n8;
        }
    }
}

/*
 * registerFunctions - This function registers your transpose
 *     functions with the driver.  At runtime, the driver will
 *     evaluate each of the registered functions and summarize their
 *     performance. This is a handy way to experiment with different
 *     transpose strategies.
 */
void registerFunctions()
{
    /* Register your solution function */
    registerTransFunction(transpose_submit, transpose_submit_desc); 

    /* Register any additional transpose functions */
    //registerTransFunction(simple_trans, simple_trans_desc); 

    //registerTransFunction(trans, trans_desc);

}

/* 
 * is_transpose - This helper function checks if B is the transpose of
 *     A. You can check the correctness of your transpose by calling
 *     it before returning from the transpose function.
 */
int is_transpose(int M, int N, int A[N][M], int B[M][N])
{
    int i, j;

    for (i = 0; i < N; i++) {
        for (j = 0; j < M; ++j) {
            if (A[i][j] != B[j][i]) {
                return 0;
            }
        }
    }
    return 1;
}

