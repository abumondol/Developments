my_res = [];
theta_vals = [30, 40, 50, 60, 70, 80, 90];

for theta_index = 3:3
    theta = theta_vals(theta_index);
    scores1 = zeros(12, 4);
    scores2 = zeros(12, 4);    
    train_test_count = zeros(12, 2);

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
            
            segs = selected_segments(s).subject(i).segments;
            indices = segs(:,1);
            labels = segs(:,2);
            indices_count = length(indices);

            subX = zeros(indices_count, 54*6);
            for j = 1:indices_count
                ix = indices(j);
                [left, right] = get_angular_boundary(Rz, ix, theta);
                
                f1 = get_features(accel(left:right, :));
                f2 = get_features(Rz(left:right, :));
                f3 = get_features(accel(left:ix, :));
                f4 = get_features(Rz(left:ix, :));
                f5 = get_features(accel(ix:right, :));            
                f6 = get_features(Rz(ix:right, :));
                subX(j, :) = [f1, f2, f3, f4, f5, f6];  
            end

            if s == i
                testX = subX;
                testY = labels;
            else
                trainX = [trainX; subX];
                trainY = [trainY; labels];
            end        
        end
        fprintf('Theta %d: Subject %d: %d, %d\n', theta, s, length(trainX), length(testX));
        train_test_count(s, :) = [length(trainX), length(testX)];

        B = TreeBagger(100, trainX(:, [1:15, 109:123]), trainY);        
        resY = predict(B, testX(:, [1:15, 109:123]));    
        resY = str2num(cell2mat(resY));        
        [TP, TN, FP, FN]= get_tf_count(testY, resY);    
        scores1(s, :) = [TP, TN, FP, FN];

        B = TreeBagger(100, trainX, trainY);        
        resY = predict(B, testX);    
        resY = str2num(cell2mat(resY));
        [TP, TN, FP, FN]= get_tf_count(testY, resY);    
        scores2(s, :) = [TP, TN, FP, FN];
        


    end

    my_res(theta_index).theta_val = theta;
    my_res(theta_index).train_test_count = train_test_count;
    my_res(theta_index).scores1 = scores1;
    my_res(theta_index).scores2 = scores2;    
end

save('my_res', 'my_res');

