function features = get_features_all_chunks(data, chunks_all)
    
    if isfield(data(1).session(1), 'annotations' )
        type = 1;
    else
        type = 0;
    end
        
    features = [];
    sub_count = length(data);    
    for sub =1:sub_count
        sess_count = length(data(sub).session);
        for sess = 1:sess_count
            fprintf('%d, %d, %d\n', type, sub, sess);
            accel = data(sub).session(sess).grav;
            chunks = chunks_all(sub).session(sess).chunks;
            
            chunk_count = size(chunks, 1);
            labels = zeros(chunk_count, 1);
            
            if isfield(data(sub).session(sess), 'annotations' )                
                annots = data(sub).session(sess).annotations;
                annots = annots(annots(:, 2)>0 & annots(:, 2)<1000, :);
                labels = chunk_annot_mapping(chunks, annots);
            end            
            
            for i = 1:chunk_count
                s = chunks(i,1);
                e = chunks(i,2);
                w = accel(s:e, 2:4);
                f = get_features_one_window(w, labels(i,1));                
                features = [features;f];
            end
        end
        
    end
    
    size(features)    
end


function f = get_features_one_window(w, label)    
    [~, min_ind] = min(w(:,1));    
    wl = w(1:min_ind, :);
    wr = w(min_ind:end, :);
    
    f = [min(w), max(w)];
    f = [f, cal_features(wl), cal_features(wr)];           
    f = [f, label]; % label
end

function f = cal_features(d)
    cv = cov(d);
    cov_xy = cv(1,2);
    cov_xz = cv(1,3);
    cov_yz = cv(2,3);
    cv = [cov_xy, cov_yz, cov_xz];
    
    f = [mean(d), std(d), skewness(d), kurtosis(d), cv];    
end

function d = first_derivative(d)
    d = d(2:end, :) - d(1:end-1, :);
end