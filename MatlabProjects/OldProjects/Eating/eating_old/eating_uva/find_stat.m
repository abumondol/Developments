gtl = csvread('ground_truth_list.csv');
sub_count = max(gtl(:,1))
stat = zeros(sub_count+2, 3);

for sub = 1:sub_count
    total = sum(gtl(:,1) == sub);
    pos = sum(gtl(:,1) == sub & gtl(:, end) == 1);
    stat(sub, :) = [sub, total, pos];
end
total = size(gtl, 1);
pos = sum(gtl(:, end) == 1);
stat(sub_count + 1, :) = [0, total, pos];

total = sum(stat(1:end-2, 2));
pos = sum(stat(1:end-2, 3));
stat(sub_count + 2, :) = [0, total, pos];

stat