function f = calculate_features_one_window(d)
    [~, ncol] = size(d);
    f=[];
    fft_f = []; 
    for i = 1:ncol
        f = [f, calculate_features_one_axis(d(:,i))];
        if mod(i,3)==0
            c= cov(d(:,i-2:i));
            f = [f, c(1,2), c(1,3), c(2,3)];
        end

        fft_f = [fft_f, calculate_features_one_axis_fft(d(:,i))];
    end

    f = [f, fft_f];
    size(f);
end

function f = calculate_features_one_axis(d)    
    f = [mean(d), var(d), rms(d), prctile(d, 25), prctile(d, 50), prctile(d, 75)]; 
end

function f = calculate_features_one_axis_fft(d)
    f = abs(fft(d));
    f = f(1:25);
    f = f';
end