
min_vals = 0:-0.5:-4
var_vals = 0.02:0.02:0.2
min_val_count = length(min_vals)
var_val_count = length(var_vals)

pos_grid = zeros(min_val_count, var_val_count);
neg_grid = zeros(min_val_count, var_val_count);

total_pos = length(th_pos_data)
total_neg = length(th_neg_data)

for i = 1 : min_val_count
    for j = 1:var_val_count
        m = min_vals(i);
        v = var_vals(j);
        
        pos_grid(i,j) = sum(th_pos_data(:, 1)>m | th_pos_data(:, 2)<v);
        neg_grid(i,j) = sum(th_neg_data(:, 1)>m | th_neg_data(:, 2)<v);        
    end
end
pos_grid = round(pos_grid/total_pos*100, 2);
neg_grid = round(neg_grid/total_neg*100, 2);

pos_grid = [ min_vals', pos_grid];
pos_grid = [ [0, var_vals]; pos_grid];

neg_grid = [ min_vals', neg_grid];
neg_grid = [ [0, var_vals]; neg_grid];

%save('pos_grid', 'pos_grid')
%save('neg_grid', 'neg_grid')




