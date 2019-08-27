proc test() {
    return 12;
}

proc main() {
    int i;
    int[4] arr;
    struct {
       float x;
       float y;
    } point;
    i = 0;
    point.x = 1.2;
    while i < 10 do {
        i = call test();
    }
    i = i + 1;
    if i > 10 {
        arr[0] = i;
    }
}
