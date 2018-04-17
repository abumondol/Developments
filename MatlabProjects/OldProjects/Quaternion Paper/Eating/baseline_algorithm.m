train_test_count = zeros(12, 2);
scores1 = zeros(12, 4);
scores2 = zeros(12, 4);
scores3 = zeros(12, 4);
for s = 1:12
    train = [];
    test = [];
    
    for i = 1:12                
        if s == i
            test = baseline_features(i).features;            
        else
            train = [train; baseline_features(i).features];            
        end        
    end
    
    fprintf('Subject %d: %d, %d\n', s, length(train), length(test));
    train_test_count(s, :) = [length(train), length(test)];
    
    continue

    B = TreeBagger(100, train(:, 1:15), train(:, end) );
    resY = predict(B, test(:, 1:15));    
    resY = str2num(cell2mat(resY));    
    [TP, TN, FP, FN]= get_tf_count(test(:,end), resY);                
    scores1(s, :) = [TP, TN, FP, FN];

    B = TreeBagger(100, train(:, 1:54), train(:, end) );
    resY = predict(B, test(:, 1:54));    
    resY = str2num(cell2mat(resY));    
    [TP, TN, FP, FN]= get_tf_count(test(:,end), resY);                
    scores2(s, :) = [TP, TN, FP, FN];

    B = TreeBagger(100, train(:, 1:end-1), train(:, end) );
    resY = predict(B, test(:, 1:end-1));    
    resY = str2num(cell2mat(resY));    
    [TP, TN, FP, FN]= get_tf_count(test(:,end), resY);                
    scores3(s, :) = [TP, TN, FP, FN];

end
return
baseline_res = [];
baseline_res(1).scores = scores1;
baseline_res(2).scores = scores2;
baseline_res(3).scores = scores3;
save('baseline_res', 'baseline_res');

