clear; load('features2');
d = features2;
bpred_loso = {};

for i = 1:1
    NumTrees = i*50;
    myres = {};
    for subj = 1:4    
        for sess = 1:5
            res = [];
            fprintf('RF %d: %d, %d\n', i, subj, sess);        
            train = d(d(:, 1) == subj & d(:,2)~=sess, :);        
            test  = d(d(:, 1) == subj & d(:,2)==sess, :);
            XTrain = train(:, 6:end-1);
            YTrain = train(:, end);
            XTest = test(:, 6:end-1);
            YTest = test(:, end);        

            res.YTest = YTest;
            
            B = TreeBagger(NumTrees, XTrain, YTrain);
            YPred = predict(B,XTest);
            YPred = cell2mat(YPred);            
            YPred = str2num(YPred);
            res.YPred = YPred;
            myres{subj, sess} = res;            
        end
    end
    
    bpred_loso{i, 1} = myres;
end

save('bpred_loso', 'bpred_loso');