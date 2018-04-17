k = 20;
g = all_grav;
test_neg = g(g(:, end) == 1, 1:3);
test_pos = g(g(:, end) == 3, 1:3);
train = g(g(:, end) == 2, 1:3);
[idx,C,sumd,D,midx,info] = kmedoids(train, k, 'Distance',@sphere_distance);
C
save('kmed','C');