d = baseline.features;
bpred_lopo = {};

for i = 1:15
    NumTrees = i*10;
    myres = {};
    for subj = 1:4    
        fprintf('RF %d: %d\n', i, subj);              
        
        train = d(d(:, 1) ~= subj, :); 
        XTrain = train(:, 3:end-1);
        YTrain = train(:, end);        
        B = TreeBagger(NumTrees, XTrain, YTrain);                
        
        
        test  = d(d(:, 1) == subj & d(:,2)<=5, :);            
        XTest = test(:, 3:end-1);
        YTest = test(:, end);        

        res = [];
        res.YTest = YTest;            

        YPred = predict(B,XTest);
        YPred = cell2mat(YPred);            
        YPred = str2num(YPred);
        res.YPred = YPred;
        myres{subj, 1} = res;            
        
    end
    
    bpred_lopo{i, 1} = myres;
end

save('bpred_lopo', 'bpred_lopo');