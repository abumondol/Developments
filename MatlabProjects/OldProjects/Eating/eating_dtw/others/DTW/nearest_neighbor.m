results = [];
offset = 40;

for s = 1:41
    fprintf('Subject %d\n', s);    
    accel = data(s).accel_norm(:, 2:4);
    mp = segments(s).min_points;
    mp_count = length(mp);
    pred = zeros(mp_count, 3);    
        
    for i =1:mp_count
        c = mp(i,1);
        label = mp(i, 2);        
        left_data = accel(c-offset:c, :);
        right_data = accel(c:c+offset, :);
    
        min_dist = inf;
        min_label = 0;
        for s2 = 1:41            
            if s2 == s
                continue
            end
                        
            mp2 = segments(s2).min_points;
            mp2 = mp2(mp2(:,3)>=0, :);
            accel2 = data(s2).accel_norm(:, 2:4);
            for j = 1:length(mp2)
                c2 = mp2(j,1);
                left_data2 = accel2(c2-offset:c2, :);
                right_data2 = accel2(c2:c2+offset, :);
                dist_left = DTW2(left_data, left_data2);
                dist_right = DTW2(right_data, right_data2);
                if dist_left + dist_right < min_dist
                    min_dist = dist_left + dist_right;
                    min_label = mp2(j, 2);
                end
            end
        end
        pred(i, 1) = label;
        pred(i, 2) = min_label;
        pred(i, 3) = min_dist;
        %fprintf('Subejct: %d :: Progress: %d/%d, => original: %d, predicted:%d, distance: %d\n', s, i, mp_count, label, min_label, min_dist);
    end
   
    results(s).pred = pred;
    [Total, TP, TN, FP, FN, precision, recall, f1] = calculate_metrics(pred);
    results(s).Total = Total;
    results(s).TP = TP;
    results(s).TN = TN;
    results(s).FP = FP;
    results(s).FN = FN;
    results(s).precision = precision;
    results(s).recall = recall;
    results(s).f1 = f1;
end