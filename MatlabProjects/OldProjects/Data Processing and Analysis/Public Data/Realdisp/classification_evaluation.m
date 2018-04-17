clear
load('features');

sec = 1;
%results = [];
NumTrees = 50;

X = features(sec).features;
Xg = features(sec).gpoints;
Y = features(sec).sub_act;        

for sub = 1:17        
    fprintf('sub:%d ... ', sub);        
            
    cond = (Y(:,1) ~= sub);
    XTrain = X(cond, :);
    XgTrain = Xg(cond, :);
    YTrain = Y(cond, 2);
    
    cond = (Y(:,1) == sub);
    XTest = X(cond, :);
    XgTest = Xg(cond, :);
    YTest = Y(cond, 2);
    
%     RFr = TreeBagger(NumTrees, XTrain(:, 1:42), YTrain, 'Method', 'classification');
%     RFl = TreeBagger(NumTrees, XTrain(:, 43:end), YTrain, 'Method', 'classification');
%     RFc = TreeBagger(NumTrees, XTrain, YTrain, 'Method', 'classification');
%             
%     [YRFr, PRFr] = predict(RFr, XTest(:, 1:42));
%     [YRFl, PRFl] = predict(RFl, XTest(:, 43:end));
%     [YRFc, PRFc] = predict(RFc, XTest);
    
%     clear RFr RFl RFc
%     fprintf('  RF done ');        
    
%     NBr = fitcnb(XgTrain(:, 4:6), YTrain, 'Prior', 'uniform');
%     NBl = fitcnb(XgTrain(:, 13:15), YTrain, 'Prior', 'uniform');
%     NBc = fitcnb(XgTrain, YTrain, 'Prior', 'uniform');
%     
%     [YNBr, PNBr, CNBr] = predict(NBr, XgTest(:, 4:6));
%     [YNBl, PNBl, CNBl] = predict(NBl, XgTest(:, 13:15));
%     [YNBc, PNBc, CNBc] = predict(NBc, XgTest);
%     
%     clear NBr NBl NBc
%     fprintf('  NB done \n');        
    
    DTr = fitctree(XgTrain(:, 4:6), YTrain, 'Prior', 'uniform');
    DTl = fitctree(XgTrain(:, 13:15), YTrain, 'Prior', 'uniform');
    DTc = fitctree(XgTrain, YTrain, 'Prior', 'uniform');
    
    [YDTr, PDTr, ~, ~] = predict(DTr, XgTest(:, 4:6));
    [YDTl, PDTl, ~, ~] = predict(DTl, XgTest(:, 13:15));
    [YDTc, PDTc, ~, ~] = predict(DTc, XgTest);
    
    
    %results(sec).subject(sub).position(1).classifier(1).Y = YRFr;
    %results(sec).subject(sub).position(1).classifier(1).P = PRFr;
    results(sec).subject(sub).position(1).classifier(2).Y = YDTr;
    results(sec).subject(sub).position(1).classifier(2).P = PDTr;    
    
    %results(sec).subject(sub).position(2).classifier(1).Y = YRFl;
    %results(sec).subject(sub).position(2).classifier(1).P = PRFl;
    results(sec).subject(sub).position(2).classifier(2).Y = YDTl;
    results(sec).subject(sub).position(2).classifier(2).P = PDTl;    
    
    %results(sec).subject(sub).position(3).classifier(1).Y = YRFc;
    %results(sec).subject(sub).position(3).classifier(1).P = PRFc;
    results(sec).subject(sub).position(3).classifier(2).Y = YDTc;
    results(sec).subject(sub).position(3).classifier(2).P = PDTc;    
    
end

save('results50', 'results');