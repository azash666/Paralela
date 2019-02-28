#include <stdio.h>
#include "mpi.h"

int main(int argc, char *argv[]) {
	int numProcs, miId, n;
	MPI_Status s;
	MPI_Init(&argc, &argv);
	MPI_Comm_size(MPI_COMM_WORLD, &numProcs);
	MPI_Comm_rank(MPI_COMM_WORLD, &miId);

	if(miId==0){
		printf("Introduce un valor entero:");
		scanf("%d", &n);
		for(int i = 1; i<numProcs; i++)
			MPI_Send ( &n , 1, MPI_INT , i, 88, MPI_COMM_WORLD );
		printf( "Soy %d y el valor leido es %d", miId, n);
	}
	else{
		MPI_Recv ( &n , 1, MPI_INT , 0, 88, MPI_COMM_WORLD , &s );
		printf( "Soy %d y el valor leido es %d", miId, n);
	}
	MPI_Finalize();
	return 0;
}
