%load('procdata');
%load('procdata_noneat');
sub_count = length(procdata);
models = [];
NumTrees = 30;

for sub = 1:sub_count
    fprintf('Classification lopo train: %d\n', sub);
    data = procdata;
    data_noneat = procdata_noneat;
    data(sub) = [];
    data_noneat(sub) = [];        
    %models(sub).model = get_model(data, data_noneat, clusters(sub).C, best_param_res(sub).results, NumTrees );     
    models(sub).model = get_model(data, data_noneat, [], best_param_res(sub).results, NumTrees );     
end

save('models', 'models');