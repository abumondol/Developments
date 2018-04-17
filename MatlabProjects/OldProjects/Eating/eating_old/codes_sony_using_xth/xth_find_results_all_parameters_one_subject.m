function results = xth_find_results_all_parameters_one_subject(data, data_noneat)
    results = [];
    i = 1;
    for xth = -2.5 : -0.5 : -4
        for step_size = 0.1 : 0.2 : 0.5
            for min_chunk_size_at_xth = 0.5 : 0.5 : 1.5
                for min_chunk_size = 0.5 : 0.5 : 1.5 
                    for max_chunk_size = 4 : 1 : 8
                        fprintf('xth:%.2f, step:%.2f, min1:%.2f, min2:%.2f, max: %.2f --- ', xth, step_size, min_chunk_size_at_xth, min_chunk_size, max_chunk_size);
                        chunks_all = xth_generate_chunks_all(data, xth, step_size, min_chunk_size_at_xth, min_chunk_size, max_chunk_size);
                        chunks_all_non_eat = xth_generate_chunks_all(data_noneat, xth, step_size, min_chunk_size_at_xth, min_chunk_size, max_chunk_size);

                        
                                                
                        [TP, FP_eat, FN] = get_chunk_stat(data, chunks_all);
                        FP_noneat = get_chunk_stat_non_eat(chunks_all_non_eat);

                        FP = FP_eat + FP_noneat;
                        P = 100*TP / (TP + FP);
                        R = 100*TP / (TP + FN);
                        F1 = 2*P*R/(P+R);

                        P_eat = 100*TP / (TP + FP_eat);
                        R_eat = 100*TP / (TP + FN); %actually R
                        F1_eat = 2*P_eat*R_eat/(P_eat+R_eat);


                        %fprintf('TP: %d, FP_eat: %d, FN: %d, FP2: %d, FP:%d\n', TP, FP_eat, FN, FP_noneat, FP);
                        fprintf('P: %.2f, R: %.2f, F1: %.2f\n', P, R, F1);

                        results(i).P = P;
                        results(i).R = R;
                        results(i).F1 = F1;
                        results(i).P_eat = P_eat;
                        results(i).R_eat = R_eat;
                        results(i).F1_eat = F1_eat;

                        results(i).TP = TP;
                        results(i).FP_eat = FP_eat;
                        results(i).FN = FN;
                        results(i).FP_noneat = FP_noneat;
                        results(i).FP = FP;

                        results(i).xth = xth;
                        results(i).step_size = step_size;
                        results(i).min_chunk_size_at_xth = min_chunk_size_at_xth;
                        results(i).min_chunk_size = min_chunk_size;
                        results(i).max_chunk_size = max_chunk_size;   
                        %results(i).chunks = chunks_all;
                        %results(i).chunks_non_eat = chunks_all_non_eat;                            
                        i = i+ 1;                                               
                    end
                end
            end
        end
    end

end


   

