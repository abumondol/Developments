format long g
all_param_res = [];

i = 1;
for xth = -2.5 : -0.25 : -4
    for step_size = 0.1 : 0.1 : 0.5
        for min_chunk_size_at_xth = 0.5 : 0.5 : 2
            for min_chunk_size = 0.5 : 0.5 : 2 
                for max_chunk_size = 3 : 1 : 7
                    fprintf('\n*** xth:%.2f, step:%.2f, min1:%.2f, min2:%.2f, max: %.2f ***\n', xth, step_size, min_chunk_size_at_xth, min_chunk_size, max_chunk_size);
                    chunks_all = xth_generate_chunks_all(data, xth, step_size, min_chunk_size_at_xth, min_chunk_size, max_chunk_size);
                    chunks_all_non_eat = xth_generate_chunks_all(data_non_eat, xth, step_size, min_chunk_size_at_xth, min_chunk_size, max_chunk_size);
                    
                    [TP, FP1, FN] = get_chunk_stat(data, chunks_all);
                    FP2 = get_chunk_stat_non_eat(chunks_all_non_eat);

                    FP = FP1 + FP2;
                    P = 100*TP / (TP + FP);
                    R = 100*TP / (TP + FN);
                    F1 = 2*P*R/(P+R);
                    
                    P2 = 100*TP / (TP + FP1);
                    R2 = 100*TP / (TP + FN); %actually R
                    F1_2 = 2*P2*R2/(P2+R2);
                    

                    fprintf('TP: %d, FP1: %d, FN: %d, FP2: %d, FP:%d\n', TP, FP1, FN, FP2, FP);
                    fprintf('P: %.2f, R: %.2f, F1: %.2f\n', P, R, F1);
                    
                    
                    all_param_res(i).P = P;
                    all_param_res(i).R = R;
                    all_param_res(i).F1 = F1;
                    all_param_res(i).P2 = P2;
                    all_param_res(i).R2 = R2;
                    all_param_res(i).F1_2 = F1_2;

                    all_param_res(i).TP = TP;
                    all_param_res(i).FP1 = FP1;
                    all_param_res(i).FN = FN;
                    all_param_res(i).FP2 = FP2;
                    all_param_res(i).FP = FP;

                    all_param_res(i).xth = xth;
                    all_param_res(i).step_size = step_size;
                    all_param_res(i).min_chunk_size_at_xth = min_chunk_size_at_xth;
                    all_param_res(i).min_chunk_size = min_chunk_size;
                    all_param_res(i).max_chunk_size = max_chunk_size;   
                    all_param_res(i).chunks = chunks_all;
                    all_param_res(i).chunks_non_eat = chunks_all_non_eat;                            
                    i = i+ 1;                                               
                end
            end
        end
    end
end


save('all_param_res','all_param_res');
   

