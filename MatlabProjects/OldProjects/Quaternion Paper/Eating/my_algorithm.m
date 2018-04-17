theta = 20;

scores = zeros(12, 8);
for s = 1:12
    trainX = [];
    testX = [];
    trainY = [];
    textY = [];
    
    for i = 1:12        
        d = data(i).data;
        Rz = d(:, 2:4);
        accel = d(:, 8:10); 
        a = data(i).annots;
        a = a(a(:,3)<=7, :);
        segs = selected_segments(s).subject(i).segments;
        indices = segs(:,1);
        labels = segs(:,2);
        indices_count = length(indices);
        
        subX = zeros(indices_count, 77);
        for j = 1:indices_count
            ix = indices(j);
            [left, right] = get_angular_boundary(Rz, ix, theta);
            f1 = get_features(accel(left:ix, :));
            f2 = get_features(Rz(left:ix, :));
            f3 = get_features(accel(ix:right, :));            
            f4 = get_features(Rz(ix:right, :));
            subX(j, :) = [f1, f2, f3, f4, Rz(ix, :), ix-left, right-ix];  
        end
        
        if s == i
            testX = subX;
            testY = labels;
        else
            trainX = [trainX; subX];
            trainY = [trainY; labels];
        end        
    end
    fprintf('Subject %d: %d, %d, %d\n', s, length(trainX), length(testX), length(a) - sum(labels));
    
    B = TreeBagger(185, trainX, trainY);
    resY = predict(B, testX);    
    resY = str2num(cell2mat(resY));
    
    [TP, TN, FP, FN1]= get_tf_count(testY, resY);
    FN = FN1 + length(a) - sum(labels);
    
    P = TP./(TP+FP);
    R = TP./(TP+FN);
    F1 = 2*(P.*R)./(P+R);
    res= [TP, TN, FP, FN, FN1, P, R, F1]    
    scores(s, :) = res;
end
