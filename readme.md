## � �������

����� ������������ �������� ����������������� ������ ������� ���������� �������� �������� (������, �����, �������, ��������) ��� ��������� ����� ���������, � ������� �������� ������� ���������. � �������� ��������� ���� ����������� ������ (`List`), �������������� �������� `ArrayList` � `LinkedList`, � ���������, � ���� ������� `HashSet` � `TreeSet`. ��� ����� ������ ����������� ���������� �� ��������, ������ ������ ������������ � �������� ����. �� ���� ������� � �������� ���� ������ ��������� ������������� ��� `Integer`.

��� ���������� �������� ����� �������������� ���� Matlab (� ������ ��������, ������� � ������ R2016b, ������ ���������� API ��� �������������� � Java-������������), ���� ��������� ������� gnuplot, ��������������� ����������� ����� ��� ����� ������ � ������ �� �������� ���������� (������� � ������ 5.0.1). � ��������� ������� ����������� ��� ���� �������; ��� ���������� ��������, ����������� ����, ������������� ������ �� ������ gnuplot.

### �������� ����������

�� **������� 1** ������������ ���������� ������ ������������ �������� ������.

![img1](../resources/images/img1.png "������� 1 - ������ ������������ �������� ������")

�� ����������� �������� �����, ��� ���������� ������ ��������� ������������� ������������� ��������������, �.�. ��� ���������� ������ ��������� ������ ������������ �������� �������� ��������, � ��� ������������� ������� - �����������.

�� **������� 2** �������� ���������� ��� �������� ������� � �������� ������.

![img2](../resources/images/img2.png "������� 2 - ������� � �������� ������")

� ����� ������� ��������� �������� ��������, ������ ��� ������������� ������� ��� �������� ����������� �������, ��� ��� ���������� ������, �������� �� ��, ��� � ������� ���������� �������� �������� ���������, ����� ���������� ����������� ������.

**������� 3** - ������� � ������ ������ ��� `LinkedList` ����������� ����� ����������, ��� ��� `ArrayList`.

![img3](../resources/images/img3.png "������� 3 - ������� � ������ ������")
 
�� **������� 4** �������� ���������� ������ �������� � ����������������� ������ � �������������� ������� `indexOf()` � `contains()` (��� ������ ���� ���������� ����������). 

![img4](../resources/images/img4.png "������� 4 - ����� � ����������������� ������")

������� ������������ ���� ���������� ���, ��� � ����������� ������� ������� ������� ������������ � ������, ������ � ��������� ������� �� ��������� � ������ ������, �� ���� ���� ����� ������ � ���� ������ ���� ����������� ������ �������� (��� ����������� �� �������� � ���� ��������� ������������� �����).

���������� ������ ������������� ����������� ��������� - �������� ����� � ��������� ������ ���������� ��������� �������, ��� � ������������ �������. ��� ���������� ���������� ������ ��� �������� ������ �����������, ����������� ��������� ��������� ������ - ������� ���� ��������� ������ � ������� ��������� (**������� 5**) :

![img5](../resources/images/img5.png "������� 5 - ������ ���� ��������� ������ � ������� ���������")

������ ����� ���������� ���������� ��������� ����������������.

### ���������

�� ������ ����� ������������� ���������� ������ ���������� ������� �������� (�������, �����, ��������) ��� ���� ���������� ��������� - `HashSet` � `TreeSet`. �� ���� ������� ���������� ������ ��������� ��������������� �������������: `O(1)` ��� `HashSet` � `O(log(n))` ��� `TreeSet`. �� **������� 6** � �������� ������� �������� ���������� �������� ������, ����������� �� ������ ����������.

![img6](../resources/images/img6.png "������� 6 - ����� � ���������")

�� ����������� ������������, ����� ������ � ���-������� �������� � 40 ��� ������, ��� ����� �������/�������� � ���.