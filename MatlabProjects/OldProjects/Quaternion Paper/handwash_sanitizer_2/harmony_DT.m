
for s = 1:10
    
    fprintf('%d\n', s);        
    train = [];
    test_hw = [];
    test_eat = [];
    for i = 1:10
        if i == s
            test_hw = features(i).hw;
            test_eat = features(i).eat;            
        else            
            train = [features(i).hw; features(i).eat];
        end
    end

    B = fitctree(train(:, 1:end-1), train(:, end));
    res_hw = predict(B, test_hw(:, 1:end-1));
    res_eat = predict(B, test_eat(:, 1:end-1));
    harmony_results(s).res_hw = res_hw;
    harmony_results(s).res_eat = res_eat;    
end

save('harmony_results', 'harmony_results');