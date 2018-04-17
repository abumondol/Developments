data = opp_data;
sub_count = length(data);

d = cell(4,1);
close all
for sub = 1:1%sub_count
    sess_count = length(data(sub).session);
    for sess = 1:sess_count
        w = windows(sub).session(sess).windows;
        w = w(w(:,6)>0, :);
        pos = 3;
        g = data(sub).session(sess).position(pos).Rz;
        
        for act = 3:3
            ix = w(w(:, 6) == act, 3);
            d{act} = [d{act}; g(ix, :)];
        end        
        
    end
    d = {d{act}};
    plot_data_bar_multi_chunks(d, '', 100);
    
    return
end