
for s = 1:10
    for ic = 1:5
        fprintf('%d, %d\n', ic, s);        
        train = [];
        test_hw = [];
        test_eat = [];
        for i = 1:10
            if i == s
                test_hw = features(i).hw;
                test_eat = features(i).eat;            
            else
                f = features(i).hw;
                selected = gen_data(s).ico(ic).selected_windows(i).hw;
                train = f(selected==1, :);
                
                f = features(i).eat;
                selected = gen_data(s).ico(ic).selected_windows(i).eat;
                train = [train; f(selected==1, :)];                
            end
        end
            
        B = fitctree(train(:, 1:end-1), train(:, end));
        res_hw = predict(B, test_hw(:, 1:end-1));
        res_eat = predict(B, test_eat(:, 1:end-1));
        my_results(s).ico(ic).res_hw = res_hw;
        my_results(s).ico(ic).res_eat = res_eat;
    end
end

save('my_results', 'my_results');