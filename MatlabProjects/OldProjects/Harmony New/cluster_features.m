
test_neg = features(features(:, end) == 1, 1:15);
test_pos = features(features(:, end) == 3, 1:15);
train = features(features(:, end) == 2, 1:15);