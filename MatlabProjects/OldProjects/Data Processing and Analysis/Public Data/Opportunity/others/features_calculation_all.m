sub_count = length(data);
features = [];
fleft = [];
fright = [];

for sub = 1:sub_count
    sess_count = length(opp_data(sub).session);
    for sess = 1:sess_count        
        fprintf('sub:%d, sess:%d\n', sub, sess);
        left_low = data(sub).session(sess).position(5).low;
        left_band = data(sub).session(sess).position(5).band;
        right_low = data(sub).session(sess).position(3).low;
        right_band = data(sub).session(sess).position(3).band;
        
        w = windows(sub).session(sess).windows;        
        count = size(w,1);        
        for i = 1:count
            s = w(i, 1);
            e = w(i, 2);
            
            label = w(i,5);
            label_left = 0;
            label_right = 0;
            if label > 0
                if w(i, 6) == 1
                    label_left = label;
                end
                
                if w(i, 7) == 1
                    label_right = label;
                end
            end
                        
            f = features_one_window(left_low(s:e, :), left_band(s:e, :));
            fleft= [fleft; f, label_left, sub, sess];
            
            f = features_one_window(right_low(s:e, :), right_band(s:e, :));
            fright = [fright; f, label_right,  sub, sess];
        end          
        
    end    
end
feaures.left = fleft;
feaures.right = fright;
save('features', 'features');
