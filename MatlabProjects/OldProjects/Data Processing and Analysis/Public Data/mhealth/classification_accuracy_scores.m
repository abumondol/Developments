function results = classification_accuracy_scores(conf_mat)

    count_by_act = sum(conf_mat, 2);    
    act_weight = count_by_act/sum(count_by_act);
    
    act_count = length(conf_mat);
    results = zeros(act_count, 6);    
    for i=1:act_count
        if sum(conf_mat(i,:)) == 0
            continue;
        end
        
        TP = conf_mat(i,i);
        FN = sum(conf_mat(i,:)) - TP;
        FP = sum(conf_mat(:,i)) - TP;
        P = 100*TP / (TP + FP);
        R = 100*TP / (TP + FN);
        F1 = 2*P*R/(P+R);
        results(i, 1)  = TP;
        results(i, 2)  = FP;
        results(i, 3)  = FN;
        results(i, 4)  = P;
        results(i, 5)  = R;
        results(i, 6)  = F1;
        
    end
    
    avg = results'* act_weight;
    results = [results;avg'];
    act_weight = [act_weight;1];
    results = [results, act_weight];    
end