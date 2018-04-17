%load('procdata');
%load('procdata_noneat');
sub_count = length(procdata);
models = [];
NumTrees = 20;

for sub = 1:sub_count
    
    data = procdata;
    data_noneat = procdata_noneat;
    data(sub) = [];
    data_noneat(sub) = [];    
    len = length(best_param_res(sub).results);
    
    models(sub).model(1).recall = best_param_res(sub).results(1).recall;
    models(sub).model(1).best_param_index = best_param_res(sub).results(1).best_index;
    models(sub).model(1).best_params = best_param_res(sub).results(1).params;
    
    models(sub).model(1).rf_model = get_model(data, data_noneat, best_param_res(sub).results(1).params, NumTrees );
    
    for i = 2:len
        fprintf('Classification subject %d, i=%d\n', sub, i);
        models(sub).model(i).recall = best_param_res(sub).results(i).recall;
        best_index  = best_param_res(sub).results(i).best_index;
        models(sub).model(i).best_param_index = best_index;
        
        if best_index == 0 || models(sub).model(i-1).best_param_index == best_index
            continue
        end
        
        models(sub).model(i).best_params = best_param_res(sub).results(i).params;
        models(sub).model(i).rf_model = get_model(data, data_noneat, best_param_res(sub).results(i).params, NumTrees );
        
    end    
    
end

save('models', 'models');