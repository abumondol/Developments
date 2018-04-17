sub_count = length(data)
min_th = -2.5;
var_th = 0.15;

segment_length = 80;
left = segment_length/2-1;
right = segment_length/2;

train_data = [];
for sub=1:sub_count
    fprintf('Subject: %d\n', sub);
    accel = data(sub).accel;
        
    c = 0;
    indices = segments_neg(sub).edges;
    mins = segments_neg(sub).min_x;
    vars = segments_neg(sub).vars;
    count = length(mins);    
    for i = 1:count
        if mins(i) <= min_th & vars(i)>=var_th            
            l = indices(i,2);
            r = indices(i,3);
            a = accel(l:r, 2:4);
            a = my_normalize(a);
            train_data = [train_data; a(:)', 0, sub];
            c = c+ 1;
        end
    end
    stat(sub,3) = count;
    stat(sub,4) = count-c;
    
    if isempty(data(sub).annots)
        continue
    end
    
    c = 0;
    indices = segments_pos(sub).edges;
    mins = segments_pos(sub).min_x;
    vars = segments_pos(sub).vars;
    count = length(mins);    
    for i = 1:count
        if mins(i) <= min_th & vars(i)>=var_th            
            l = indices(i,2);
            r = indices(i,3);
            a = accel(l:r, 2:4);
            a = my_normalize(a);
            train_data = [train_data; a(:)', 1, sub];
            c = c +1;
        end
    end
    stat(sub,1) = count;
    stat(sub,2) = count-c;
    
end
size(train_data)

csvwrite('segment_data/train_data.csv', train_data);