%load('procdata');
%load('procdata_noneat');
sub_count = length(procdata);
lopo_test_results = [];

for sub = 1:sub_count
    fprintf('Classification test %d\n', sub);
    data = procdata(sub);
    data_noneat = procdata_noneat(sub);
    params = best_param_res(sub).results;
    model = models(sub).model.rf_model;
    
    [conf_mat, stats] = test_model(data, data_noneat, params, model);
    lopo_test_results(sub).conf_mat = conf_mat;
    lopo_test_results(sub).stats = stats;
    
end

save('lopo_test_results', 'lopo_test_results');