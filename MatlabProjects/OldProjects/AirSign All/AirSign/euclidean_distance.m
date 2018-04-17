function d = euclidean_distance(a, b)
    d = (a-b);
    d = sum(d.*d,2);
    d= sqrt(d);
end