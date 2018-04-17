sub_count = length(data)
min_th = -2.5;
var_th = 0.15;

segment_length = 80;
left = segment_length/2-1;
right = segment_length/2;
offset = 24;

train_data_left = [];
train_data_right = [];
for sub=1:sub_count
    fprintf('Subject: %d\n', sub);
    accel = data(sub).accel;
    a = segments(sub).annot_indices;
    b = a(a(:, 2) < 400, :);
    d = a(a(:, 2) >= 400 & a(:, 2) <1000, :);
        
    c = 0;
    seg = segments(sub).segments;    
    count = length(seg);
    
    for i = 1:count
        if seg(i, 2)<=-2.5 && seg(i,3)>=0.2             
            ind = seg(i,1);            
            sb = find(b(:,1)>= ind - 40 & b(:,1)<=ind+40);
            sd = find(d(:,1)>= ind - 64 & d(:,1)<=ind+64);
            if isempty(sb) && isempty(sd)
                dt = accel(ind-39:2:ind, 2:4);
                %dt = my_normalize(dt);                
                train_data_left = [train_data_left; dt(:)', 0, sub];
                
                dt = accel(ind:2:ind+39, 2:4);
                %dt = my_normalize(dt);
                dt = flipud(dt);
                train_data_right = [train_data_right; dt(:)', 0, sub];
                
            else 
                sb = find(b(:,1)>= ind - 24 & b(:,1)<=ind+24);
                if ~isempty(sb) 
                    dt = accel(ind-39:2:ind, 2:4);
                    %dt = my_normalize(dt);
                    train_data_left = [train_data_left; dt(:)', 1, sub];
                    
                    dt = accel(ind:2:ind+39, 2:4);
                    %dt = my_normalize(dt);
                    dt = flipud(dt);
                    train_data_right = [train_data_right; dt(:)', 1, sub];                    
                end
             
            end
            
        end
    end
    
end
[size(train_data_left); size(train_data_right)]
[sum(train_data_left(:, end-1)); sum(train_data_right(:, end-1))]

csvwrite('segment_data/train_data_left.csv', train_data_left);
csvwrite('segment_data/train_data_right.csv', train_data_right);