package gpu;

public enum JobStatus {
	QUEUED, //waiting for a free GPU
	RUNNING, //currently using a GPU
	COMPLETED, //finished its required runtime
	CANCELLED //stopped before completion
}
