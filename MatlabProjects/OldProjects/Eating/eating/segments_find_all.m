sub_count = length(data);
window_length = 48;
slide_length = window_length/3;

segments = [];
for sub = 1:sub_count
    fprintf('Subject: %d\n', sub);
    accel = data(sub).accel;
    annot_indices = get_annot_indices(accel, data(sub).annots_adjusted);
    if ~isempty(data(sub).annots_adjusted)
        annot_indices = [annot_indices, data(sub).annots_adjusted(:,1)];
    end
    
    x = data(sub).accel(:, 2);
    count = length(x);
    seg = [];
    for i = 1:slide_length:count-window_length
        [m, I] = min(x(i:i+window_length-1));
        absolute_I = i+I-1;
        if I >= slide_length+1 && I <= window_length - slide_length && absolute_I-39>= 1 && absolute_I + 40 <=count
            a = accel(absolute_I-39:absolute_I+40, 2:4);
            v = sum(var(a));            
            seg = [seg; absolute_I, m, v, i];
            if x(i+I-1) ~= m
                fprintf('Minimum index mismatch');
                return
            end
        end        
    end
    segments(sub).segments = seg;
    segments(sub).annot_indices = annot_indices;
end

save('segments', 'segments');
