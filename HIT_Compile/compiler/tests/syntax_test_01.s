proc main() {
    int i;
    int[4] arr;
    struct {
       float x;
       float y;
    } point;
    i = 0;
    while i < 10 do {
        call printf("%d", i);
    }
    i = i + 1;
    if i > 10 {
        arr[0] = i;
    }
}