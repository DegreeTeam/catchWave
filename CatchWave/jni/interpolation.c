#include "com_catchwave_jni_DSPforJNI.h"

/*
 * Linear Interpolation Filter Module
 */

void javaLinear_interpolation_filter(float* data) {
	int i, j;
	float tmp;
	for (i = 0; i < DOWN_FACTOR - 1; i++) {
		for (j = 0; j < DataLen; j++) {
			tmp = data[DOWN_FACTOR * (j + 1) - 1] - data[DOWN_FACTOR * (j) - 1];
			tmp *= (i + 1);
			tmp /= DOWN_FACTOR;
			data[DOWN_FACTOR * j + i] = data[DOWN_FACTOR * j + i - 1] + tmp;
		}
	}
	data[0] = data[1];
}

