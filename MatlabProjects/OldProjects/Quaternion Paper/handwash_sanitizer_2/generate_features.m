window_size = 150;
slide = 25;
for s = 1:10
    fprintf('%d\n', s);
    ai = data(s).annot_indices;    
    a  = ai(:,1) - 25;
    b  = ai(:,2) + 25;

    d = data(s).hw_data(:, 5:13);
    count = length(d);
    fcount = length(1:slide:count-window_size+1);
    f = zeros(fcount, 289);
    for i = 1:fcount
        ix = (i-1)*(window_size/2)+1;
        label = sum(ix>=a & ix<=b);
        if label>1
            fprintf('label >1');
            return
        end
        f(i, :) = [calculate_features_one_window(d(ix:ix+49, :)), label];
    end
    features(s).hw = f;

    d = data(s).eat_data(:, 5:13);
    count = length(d);
    fcount = length(1:slide:count-window_size+1);
    f = zeros(fcount, 289);
    for i = 1:fcount
        ix = (i-1)*(window_size/2)+1;        
        f(i, :) = [calculate_features_one_window(d(ix:ix+49, :)), 0];
    end
    features(s).eat = f;
end

save('features', 'features');