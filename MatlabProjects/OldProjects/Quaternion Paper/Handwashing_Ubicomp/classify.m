res = [];

for i = 1:2
    f = features(i).features;    
    for h =1:2
        sub_count = length(ds(i).subject);
        for s =1:sub_count
            fprintf('ds:%d, h:%d, sub:%d\n', i, h, s);

            train = f(f(:,end)~=s & f(:,end-1)==h, :);
            test = f(f(:,end)==s & f(:,end-1)==h, :);
            
            B = fitctree(train(:, 1:63), train(:, end-2));
            [resY, scores, cost] = predict(B, test(:, 1:63));    
            %resY = str2num(cell2mat(resY));    
            [TP, TN, FP, FN]= get_tf_count(test(:,end-2), resY);                
            res(i).hand(h).subject(s).algo1 = [TP, TN, FP, FN]; 
            
            
            [model, llh] = logitBin(train(:, 64:225), train(:, end-2));
            [y, p] = logitBinPred(model, test(:, 64:225));            
            scores = scores + [(1-p), p];
            resY = scores(:,2) >=scores(:,1);
            [TP, TN, FP, FN]= get_tf_count(test(:,end-2), resY);                
            res(i).hand(h).subject(s).algo2 = [TP, TN, FP, FN]; 
            return
            
        end
    end
end

save('res', 'res');

