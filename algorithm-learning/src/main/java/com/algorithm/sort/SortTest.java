package com.algorithm.sort;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vector
 * @date: 2018/12/4 0004 10:07
 * http://blog.damonare.cn/2016/12/20/%E5%8D%81%E5%A4%A7%E7%BB%8F%E5%85%B8%E6%8E%92%E5%BA%8F%E7%AE%97%E6%B3%95%E6%80%BB%E7%BB%93%EF%BC%88javascript%E6%8F%8F%E8%BF%B0%EF%BC%89/
 * https://mp.weixin.qq.com/s/b1T4yFGf98z4Zqe0cMrbrg
 */
public class SortTest {

	public static void main(String[] args) {
		int arr[] = {3, 44, 38, 5, 47, 36, 36, 26, 27, 2, 46, 4, 19, 50, 48};
		long s = System.nanoTime();
		//        bubbleSort(arr);
		//        selectionSort(arr);
		//		  insertionSort(arr);
		//        shellSort(arr);
		//        mergeSort(arr, 0, arr.length - 1);
		//        heapSort(arr);
		//        quickSort(arr, 0, arr.length - 1);
		//		countSort(arr);
//		bucketSort(arr);
		//		  countSort(arr);
//		bucketSort(arr);
		radixSort(arr,100);
		long e = System.nanoTime();
		//		print(ints, (e - s) / 1000_000); //桶排序较特殊
		print(arr, (e - s) / 1000);
	}


	/**
	 *
	 * 基数排序
	 *基数排序基于分别排序，分别收集，所以是稳定的。但基数排序的性能比桶排序要略差，每一次关键字的桶分配都需要O(n)的时间复杂度，而且分配之后得到新的关键字序列又需要O(n)的时间复杂度。假如待排数据可以分为d个关键字，则基数排序的时间复杂度将是O(d*2n) ，当然d要远远小于n，因此基本上还是线性级别的。
	 *
	 * 基数排序的空间复杂度为O(n+k)，其中k为桶的数量。一般来说n>>k，因此额外空间需要大概n个左右。
	 *
	 * @param arr 数组
	 * @param numOfDigits 最大的位数
	 */
	private static void radixSort(int[] arr,int numOfDigits) {
		int n = 1;
		int k = 0;
		int length = arr.length;
		int[][] bucket = new int[10][length];
		int[] order = new int[length];
		while (n < numOfDigits) {
			for (int num : arr) {
				int digit = (num / n) % 10;
				bucket[digit][order[digit]] = num;
				order[digit]++;
			}
			for (int i = 0; i < length; i++) {
				if (order[i] != 0) {
					for (int j = 0; j < order[i]; j++) {
						arr[k] = bucket[i][j];
						k++;
					}
				}
				order[i] = 0;
			}
			n*=10;
			k=0;
		}
	}


    /**
	 * 技术排序
	 * 找出待排序的数组中最大和最小的元素；
	 * 统计数组中每个值为i的元素出现的次数，存入数组C的第i项；
	 * 对所有的计数累加（从C中的第一个元素开始，每一项和前一项相加）；
	 * 反向填充目标数组：将每个元素i放在新数组的第C(i)项，每放一个元素就将C(i)减去1
	 *
	 * @param arr
	 */
	private static void countSort(int[] arr) {
		int maxValue = 0;
		for (int i = 0; i < arr.length; i++) {
			if (maxValue < arr[i])
				maxValue = arr[i];
		}
		int bucket[] = new int[maxValue + 1];
		for (int i = 0; i < arr.length; i++) {
			bucket[arr[i]] = bucket[arr[i]] + 1;
		}
		int sortedIndex = 0;
		for (int i = 0; i < bucket.length; i++) {
			while (bucket[i] > 0) {
				arr[sortedIndex++] = i;
				bucket[i] = bucket[i] - 1;
			}
		}
	}

	/**
	 * 桶排序
	 * 技术排序的升级版，缩小了额外的使用空间
     * 主要：1、确定桶的个数；2、确定命中算法
	 * @param arr
	 * @return
	 */
	private static void bucketSort(int[] arr) {
		int max = arr[0], min = arr[0];
		// 找出最大和最小值
		for (int i : arr) {
			if (max < i) {
				max = i;
			}
			if (min > i) {
				min = i;
			}
		}
		int DEFAULT_BUCKET_SIZE = 5;
        /**
         *
         * 3, 44, 38, 5, 47, 36, 36, 26, 27, 2, 46, 4, 19, 50, 48
         *
         * min = 3,max = 50
         * bucketNum = 50/5 - 3/5 + 1 = 11
         *
         * 0  1  2  3  4  5  6  7  8  9  10
         * 3        38 44
         * 5     26 36 47
         * 2     27    46
         * 4  19       50
         *             48
         *
         *
         *
         *
         *
         *
         *
         *
         *
         */




		// 确定桶的数量
		int bucketNum = max / DEFAULT_BUCKET_SIZE - min / DEFAULT_BUCKET_SIZE + 1;
		// 创建bucket
		ArrayList<List<Integer>> buckList = new ArrayList<>();
		for (int i = 0; i < bucketNum; i++) {
			buckList.add(new ArrayList<>());
		}
		for (int i = 0; i < arr.length; i++) {
			buckList.get((arr[i] - min) / DEFAULT_BUCKET_SIZE).add(arr[i]);

		}
		int index = 0;
		for (int i = 0; i < bucketNum; i++) {
			List<Integer> list = buckList.get(i);
			int[] temp = list2arr(list);
			insertionSort(temp);
			for (int k : temp) {
				arr[index++] = k;
			}
		}
	}

	private static int[] list2arr(List<Integer> list) {
		int[] result = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			result[i] = list.get(i);
		}
		return result;
	}


	/**
	 * 快速排序
	 * 从数列中挑出一个元素，称为 “基准”（pivot）；
	 *
	 * 重新排序数列，所有元素比基准值小的摆放在基准前面，所有元素比基准值大的摆在基准的后面（相同的数可以到任一边）。在这个分区退出之后，该基准就处于数列的中间位置。这个称为分区（partition）操作；
	 *
	 * 递归地（recursive）把小于基准值元素的子数列和大于基准值元素的子数列排序
	 * @param arr
	 * @param left
	 * @param right
	 */
	private static void quickSort(int[] arr, int left, int right) {
		if (left >= right || arr == null || arr.length <= 1) {
			return;
		}
		int i = left, j = right, pivot = arr[(j + i) / 2];
		while (i <= j) {
			while (arr[i] < pivot) {
				i++;
			}
			while (arr[j] > pivot) {
				j--;
			}
			if (i < j) {
				int temp = arr[i];
				arr[i] = arr[j];
				arr[j] = temp;
				i++;
				j--;
			} else if (i == j) {
				i++;
			}
		}
		quickSort(arr, i, right);
		quickSort(arr, left, j);
	}


	/**
	 * 堆排序
	 * https://www.cnblogs.com/chengxiao/p/6129630.html
	 * 1、将无序列数组构建成一个堆，根据升序或降序需求选择大顶堆(每个结点的值都大于或等于其左右孩子结点的值)或小顶堆(每个结点的值都小于或等于其左右孩子结点的值)
	 * 2、将末尾元素摘出来与堆顶元素交换，末尾元素即当前堆结构中的最大的元素
	 * 3、重新调整结构，使其满足堆定义，然后继续交换堆顶元素与当前末尾元素，反复执行调整+交换步骤，直到整个序列有序
	 *
	 * @param arr
	 */
	private static void heapSort(int[] arr) {
		// 1、构建大顶堆
		for (int i = arr.length / 2 - 1; i >= 0; i--) {
			//从第一个非叶子结点从下至上，从右至左调整结构
			adjustHeap(arr, i, arr.length);
		}
		//2.调整堆结构+交换堆顶元素与末尾元素
		for (int j = arr.length - 1; j > 0; j--) {
			swap(arr, 0, j);//将堆顶元素与末尾元素进行交换
			adjustHeap(arr, 0, j);//重新对堆进行调整
		}

	}

	/**
	 * 交换元素
	 *
	 * @param arr
	 * @param a
	 * @param b
	 */
	private static void swap(int[] arr, int a, int b) {
		int temp = arr[a];
		arr[a] = arr[b];
		arr[b] = temp;
	}

	/**
	 * 调整大顶堆
	 *
	 * @param arr
	 * @param i
	 * @param length
	 */
	private static void adjustHeap(int[] arr, int i, int length) {
		int temp = arr[i];//先取出当前元素i
		for (int j = i * 2 + 1; j < length; j = j * 2 + 1) {//从i结点的左子结点开始，也就是2i+1处开始
			if (j + 1 < length && arr[j] < arr[j + 1]) {//如果左子结点小于右子结点，k指向右子结点
				j++;
			}
			if (arr[j] > temp) {//如果子节点大于父节点，将子节点值赋给父节点（不用进行交换）
				arr[i] = arr[j];
				i = j;
			} else {
				break;
			}
		}
		arr[i] = temp;//将temp值放到最终的位置
	}

	/**
	 * 归并排序
	 * 把长度为n的输入序列分成两个长度为n/2的子序列；
	 * 对这两个子序列分别采用归并排序；
	 * 将两个排序好的子序列合并成一个最终的排序序列。
	 *
	 * @param arr
	 * @param L
	 * @param R
	 */
	private static void mergeSort(int[] arr, int L, int R) {
		//如果只有一个元素，那就不用排序了
		if (L == R) {
			return;
		}
		//取中间的数，进行拆分
		int M = (L + R) / 2;
		//左边的数不断进行拆分
		mergeSort(arr, L, M);
		//右边的数不断进行拆分
		mergeSort(arr, M + 1, R);
		//合并
		merge(arr, L, M + 1, R);
	}

	/**
	 * @param arr
	 * @param L   指向数组第一个元素
	 * @param M   指向数组分隔的元素
	 * @param R   指向数组最后的元素
	 */
	private static void merge(int[] arr, int L, int M, int R) {
		//左边的数组的大小
		int leftArr[] = new int[M - L];
		//右边的数组大小
		int rightArr[] = new int[R - M + 1];
		//往这两个数组填充数据
		for (int i = L; i < M; i++) {
			leftArr[i - L] = arr[i];
		}
		for (int i = M; i < R; i++) {
			rightArr[i - M] = arr[i];
		}
		int i = 0, j = 0;
		// arrays数组的第一个元素
		int k = L;
		//比较这两个数组的值，哪个小，就往数组上放
		while (i < leftArr.length && j < rightArr.length) {
			//谁比较小，谁将元素放入大数组中,移动指针，继续比较下一个
			if (leftArr[i] < rightArr[j]) {
				arr[k] = leftArr[i];
				i++;
				k++;
			} else {
				arr[k] = rightArr[j];
				j++;
				k++;
			}
		}
		//如果左边的数组还没比较完，右边的数都已经完了，那么将左边的数抄到大数组中(剩下的都是大数字)
		while (i < leftArr.length) {
			arr[k] = leftArr[i];
			i++;
			k++;
		}
		while (j < rightArr.length) {
			arr[k] = rightArr[j];
			k++;
			j++;
		}
	}

	/**
	 * 希尔排序又叫缩小增量排序
	 * 选择一个增量序列t1，t2，…，tk，其中ti>tj，tk=1；
	 * 按增量序列个数k，对序列进行k 趟排序；
	 * 每趟排序，根据对应的增量ti，将待排序列分割成若干长度为m 的子序列，分别对各子表进行直接插入排序。仅增量因子为1 时，整个序列作为一个表来处理，表长度即为整个序列的长度。
	 *
	 * @param arr
	 */
	private static void shellSort(int[] arr) {
		int number = arr.length / 2;
		int i, j, temp;
		while (number >= 1) {
			for (i = number; i < arr.length; i++) {
				temp = arr[i];
				j = i - number;
				while (j >= 0 && arr[j] > temp) {
					arr[j + number] = arr[j];
					j = j - number;
				}
				arr[j + number] = temp;
			}
			number = number / 2;
		}

	}

	/**
	 * 插入排序
	 * 从第一个元素开始，该元素可以认为已经被排序；
	 * 取出下一个元素，在已经排序的元素序列中从后向前扫描；
	 * 如果该元素（已排序）大于新元素，将该元素移到下一位置；
	 * 重复步骤3，直到找到已排序的元素小于或者等于新元素的位置；
	 * 将新元素插入到该位置后；
	 * 重复步骤2~5。
	 * <p>
	 * 最好手动操作下
	 *
	 * @param arr
	 */
	private static void insertionSort(int[] arr) {
		int len = arr.length;
		int preIndex, current;
		for (int i = 1; i < len; i++) {
			preIndex = i - 1;
			current = arr[i];
			while (preIndex >= 0 && arr[preIndex] > current) {
				arr[preIndex + 1] = arr[preIndex];
				preIndex--;
			}
			arr[preIndex + 1] = current;
		}
	}

	/**
	 * 选择排序
	 * 初始状态：无序区为R[1..n]，有序区为空；
	 * 第i趟排序(i=1,2,3…n-1)开始时，当前有序区和无序区分别为R[1..i-1]和R(i..n）。
	 * 该趟排序从当前无序区中-选出关键字最小的记录 R[k]，将它与无序区的第1个记录R交换，使R[1..i]和R[i+1..n)分别变为记录个数增加1个的新有序区和记录个数减少1个的新无序区；
	 * n-1趟结束，数组有序化了。
	 *
	 * @param arr
	 */
	private static void selectionSort(int[] arr) {
		int minIndex, temp = 0;
		for (int i = 0; i < arr.length - 1; i++) {
			minIndex = i;
			for (int j = i + 1; j < arr.length; j++) {
				if (arr[minIndex] > arr[j]) {
					minIndex = j;
				}
			}
			temp = arr[i];
			arr[i] = arr[minIndex];
			arr[minIndex] = temp;
		}
	}

	/**
	 * 冒泡排序
	 * 比较相邻的元素。如果第一个比第二个大，就交换它们两个；
	 * 对每一对相邻元素作同样的工作，从开始第一对到结尾的最后一对，这样在最后的元素应该会是最大的数；
	 * 针对所有的元素重复以上的步骤，除了最后一个；
	 * 重复步骤1~3，直到排序完成。
	 *
	 * @param
	 */
	private static void bubbleSort(int[] arr) {
		int temp = 0;
		for (int i = 0; i < arr.length - 1; i++) {
			for (int j = i + 1; j < arr.length; j++) {
				if (arr[i] > arr[j]) {
					temp = arr[i];
					arr[i] = arr[j];
					arr[j] = temp;
				}
			}
		}
	}

	private static void print(int[] newArr, long millisecond) {
		for (int i : newArr) {
			System.out.print(i + ", ");
		}
		System.out.print("耗时: " + millisecond);
		System.out.println();
	}
}