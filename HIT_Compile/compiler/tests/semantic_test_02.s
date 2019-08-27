proc sum(int a, int b) -> int {
    return a + b;
}

proc test_struct() {
    struct {
       float x;
       float y;
    } point;
    point.x = 1.2;
    point.y = 1.3;
}

proc main() {
    int i;
    int total;
    int[10] arr;
    i = 0;
    total = 0;
    while i < 10 do {
        total = call sum(total, i);
        i = i + 1;
        arr[i] = total;
    }
    if !(((i + 20) * 3 / 2) == 45) {
        call test_struct();
    } else {
        i = -1;
    }
}
