sub_count = 52; %length(data);

res = zeros(sub_count, 4);
for s = 1:sub_count
    res(s, 1) = segments_for_pat(s).gt_eat_count;
    res(s, 2) = segments_for_pat(s).eat_count;
    res(s, 3) = segments_for_pat(s).eat_lost;
    res(s, 4) = segments_for_pat(s).neg_count;
end
sum(res)

