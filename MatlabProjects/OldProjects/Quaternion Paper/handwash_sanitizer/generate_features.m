window_size = 150;
window_size_2 = 75;
slide = 25;
for s = 1:10
    fprintf('%d\n', s);
    ai = data(s).annot_indices;    
    a  = ai(:,1) - window_size_2;
    b  = ai(:,2) + window_size_2;

    d = data(s).hw_data(:, 5:13);
    count = length(d);
    fcount = length(1:slide:count-window_size);
    f = zeros(fcount, 290);
    for i = 1:fcount
        ix = (i-1)*slide+1;
        label = sum(ix>=a & ix<=b);
        if label>1
            fprintf('label >1');
            return
        end
        f(i, :) = [ix, calculate_features_one_window(d(ix:ix+window_size-1, :)), label];
    end

    features(s).hw = f;

    d = data(s).eat_data(:, 5:13);
    count = length(d);
    fcount = length(1:slide:count-window_size+1);
    f = zeros(fcount, 290);
    for i = 1:fcount
        ix = (i-1)*slide+1;        
        f(i, :) = [ix, calculate_features_one_window(d(ix:ix+window_size-1, :)), 0];
    end
    features(s).eat = f;
end

save('features', 'features');