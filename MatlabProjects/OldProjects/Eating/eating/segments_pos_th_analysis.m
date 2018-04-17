%load('data');
sub_count = length(segments_pos);
vals = [];

for sub = 1:sub_count    
    min_x = segments_pos(sub).min_x;
    vars = segments_pos(sub).vars;
    vals= [vals; zeros(length(min_x), 1)+sub, min_x, vars];
end

length(vals)
min_x = vals(:,2);
vars = vals(:, 3);
sum(min_x <= - 2 )
sum(vars >= 0.1)
sum(min_x <= - 2 & vars >= 0.05)
[N, E] = histcounts(vars, 1000);
N = [N; E(1:end-1); E(2:end)]';
histogram(min_x, 1000);
return

for sub = 1:sub_count
    tr = vals(vals(:,1) ~=sub, 2:3);
    ts = vals(vals(:,1) ==sub, 2:3);
    
    close all
    figure
    histogram(tr(:,1));
    hold on
    histogram(ts(:,1));
    title(num2str(sub))
    waitforbuttonpress    
end

