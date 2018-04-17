%load('all_param_res');
sub_count = length(all_param_res);
best_param_res = [];

for sub = 1:sub_count
    fprintf('Subejct %d\n', sub);
    best_param_res(sub).results = xth_find_best_parameters_one_subject(all_param_res(sub).results);
end

save('best_param_res','best_param_res');