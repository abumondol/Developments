load('all_param_res');
count = length(all_param_res);

R = [all_param_res.R];
[M,I] = max(R);
best_param_res = [];
for recall=99:-1:90
    best_index = 0;
    best_F1 = 0;
    best_precision = 0;
    for j = 1:count
        res = all_param_res(j);       
        if res.R >= recall && res.P > best_precision%res.F1 > best_F1
            best_index = j;
            %best_F1 = res.F1;
            best_precision = res.P;
        end
    end    
    
    best_param_res(100-recall).recall = recall;
    if best_index ~= 0
        best_param_res(100-recall).best_index = best_index;    
        best_param_res(100-recall).params = all_param_res(best_index);
    end
end

if isempty(best_param_res(1).best_index)
    best_param_res(1).recall = M;
    best_param_res(1).best_index = I;
    best_param_res(1).params = all_param_res(I);
else
    best_param_res(11).recall = M;
    best_param_res(11).best_index = I;
    best_param_res(11).params = all_param_res(I);
end

save('best_param_res', 'best_param_res');

