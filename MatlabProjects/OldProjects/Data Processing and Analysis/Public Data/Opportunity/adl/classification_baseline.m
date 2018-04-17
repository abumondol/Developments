clear; load('../oppdata'); load('features');

NumTrees = 50;

base_pred = cell(4, 5);
for subj=1:4
    for sess=1:5        
        [train, test] = get_train_test(subj, sess, features);        
        XTrain = train(:, 4:end-1);
        YTrain = train(:, end);    
        B = TreeBagger(NumTrees, XTrain, YTrain);

        fprintf(' %d, %d\n', subj, sess);
        XTest = features{subj, sess}.xy(:, 4:end-1);
        YTest = features{subj, sess}.xy(:, end);        

        YPred = predict(B, XTest);
        YPred = cell2mat(YPred);            
        YPred = str2num(YPred);
        
        res = [];
        res.YTest = YTest;
        res.YPred = YPred;
        
        base_pred{subj, sess} = res;
    end
end

save('base_pred', 'base_pred') ;