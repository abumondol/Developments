function best_param_res = xth_find_best_parameters_one_subject(all_param_res)

   
    best_recall = 0;
    best_precision = 0;
    best_index = 0;
    count = length(all_param_res);
    for i = 1:count
        if all_param_res(i).R > best_recall || ( all_param_res(i).R == best_recall && all_param_res(i).P > best_precision )     
            best_recall = all_param_res(i).R;
            best_precision = all_param_res(i).P;
            best_index = i;
        end
    end
    
    best_param_res = all_param_res(best_index);
    return

    count = length(all_param_res);
    R = [all_param_res.R];
    best_param_res = [];
    
    [M,I] = max(R);    
    best_param_res(1).recall = M;
    best_param_res(1).best_index = I;
    best_param_res(1).params = all_param_res(I);
    
    for recall = 99 : -1 : 91
        best_index = 0;
        best_F1 = 0;
        best_precision = 0;
        for j = 1:count
            res = all_param_res(j);       
            if res.R >= recall && res.P > best_precision%res.F1 > best_F1
                best_index = j;
                best_F1 = res.F1;
                best_precision = res.P;
            end
        end    

        best_param_res(100 - recall + 1).recall = recall;
        best_param_res(100 - recall + 1).best_index = best_index;    
        if best_index ~= 0            
            best_param_res(100-recall + 1).params = all_param_res(best_index);
        end
    end   
    
end
