d = baseline.features;
bres = [];

for subj = 1:4
    f = d(d(:, 1) == subj, :);
    for sess = 1:5
        XTrain = f(f(:,2)~=sess, 4:18);
        YTrain = f(f(:,2)~=sess, end);
        XTest = f(f(:,2)==sess, 4:18);
        
        for NumTrees = 50:50:250
            fprintf('RF: %d, %d, %d\n', subj, sess, NumTrees);
            B = TreeBagger(NumTrees,XTrain,YTrain);
            Yfit = predict(B,XTest);
            bres(subj).session(sess).ntree(NumTrees/50).y = Yfit;
        end        
        
    end
end