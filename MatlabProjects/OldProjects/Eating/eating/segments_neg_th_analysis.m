%load('data');
sub_count_pos = length(segments_pos)
vals_pos = [];
for sub = 1:sub_count_pos
    min_x = segments_pos(sub).min_x;
    vars = segments_pos(sub).vars;
    vals_pos= [vals_pos; zeros(length(min_x), 1)+sub, min_x, vars];    
end

sub_count = length(segments_neg)
vals_neg = [];
for sub = sub_count_pos+1:sub_count       
    min_x = segments_neg(sub).min_x;
    vars = segments_neg(sub).vars;
    vals_neg= [vals_neg; zeros(length(min_x), 1)+sub, min_x, vars];
end

min_vals = 0:-0.5:-4
var_vals = 0.02:0.02:0.2
min_val_count = length(min_vals)
var_val_count = length(var_vals)

pos_grid = zeros(min_val_count, var_val_count);
neg_grid = zeros(min_val_count, var_val_count);
combo_grid = [];

total_pos = length(vals_pos)
total_neg = length(vals_neg)

for i = 1 : min_val_count
    for j = 1:var_val_count
        m = min_vals(i);
        v = var_vals(j);
        
        pos_grid(i,j) = sum(vals_pos(:, 2)>m | vals_pos(:, 3)<v);
        neg_grid(i,j) = sum(vals_neg(:, 2)>m | vals_neg(:, 3)<v);
        combo_grid = [combo_grid; m, v, pos_grid(i,j), neg_grid(i,j)];        
    end
end
pos_grid = round(pos_grid/total_pos*100, 2);
neg_grid = round(neg_grid/total_neg*100, 2);

pos_grid = [ min_vals', pos_grid];
pos_grid = [ [0, var_vals]; pos_grid];

neg_grid = [ min_vals', neg_grid];
neg_grid = [ [0, var_vals]; neg_grid];

save('pos_grid', 'pos_grid')
save('neg_grid', 'neg_grid')
return
figure
bar3(pos_grid')

figure
bar3(neg_grid')



