function f = get_test_data(accel, window_length, step_size, minx_th, var_th)
    count = length(accel);
    l = window_length/2-1;
    r = window_length/2;
    f = [];
    for ind = window_length/2:step_size:count-window_length/2
        d = accel(ind-l:ind+r, :);
        m = min(d(:, 1));
        s = var(d);
        s = sum(s);
        if m <= minx_th && s>=var_th            
            f = [f; get_features(d), ind];
        end
    end
end