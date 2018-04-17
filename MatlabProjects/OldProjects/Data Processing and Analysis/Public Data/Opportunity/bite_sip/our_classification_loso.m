clear; load('../oppdata'); load('indices'); 

%mg_all = get_mid_gravity_values(data, indices);

theta = 70;
cos_theta = cos(degtorad(theta));
max_len = 9;
xmins = 0.2;
ntree = 10:10:100;

theta_count = length(theta);
maxlen_count = length(max_len);
xmin_count = length(xmins);
ntree_count = length(ntree);

%res_loso = {};

for xix = 1:xmin_count
    for ntix = 1:ntree_count
        for mlix = 1:maxlen_count
            for ttix = 1:theta_count
                r = cos_theta(ttix);
                tpfpfnfn1  = zeros(1, 4);
                
                for subj = 1:4
                    for sess = 1:5
                        fprintf('%d, %d, :: %d, %d:: %d, %d\n', xix, ntix, mlix, ttix);
                        
                        XTrain = [];
                        YTrain =[];
                        XTest = [];
                        YTest = [];
                        fn1 = 0;
                        
                        for sess2 = 1:6                            
                            a = oppdata{subj, sess2}.accel;
                            g = oppdata{subj, sess2}.grav;
                            
                            segs = our_find_segments_xth(g, xmins(xix));
                            bp = our_find_bite_points(g(:,1), segs);
                            window_indices = our_find_windows(g, bp, r, max_len(mlix)*30);

                            mid_ix = indices(indices(:,1)==subj & indices(:,2)==sess2, 5);
                            res = our_find_labels(window_indices, mid_ix);

                            fprintf('    Sess 2: %d, :: %d\n', sess2, fn1);

                            f = our_feature_calculation(a, window_indices);
                            if sess2==sess                    
                                XTest = f;
                                YTest = res.labels;
                                fn1 = res.missed;
                            else      
                                XTrain = [XTrain; f];
                                YTrain = [YTrain; res.labels];
                            end

                        end
                        
                        B = TreeBagger(ntree(ntix), XTrain, YTrain);
                        YPred = predict(B,XTest);
                        YPred = cell2mat(YPred);            
                        YPred = str2num(YPred);
                        
                        [TP, FP, FN] = find_tpfpfn(YTest, YPred);
                        tpfpfnfn1 = tpfpfnfn1 + [TP, FP, FN, fn1];
                    end
                end
                
                z = tpfpfnfn1;
                z(3) = z(3) + z(4);
                [PR, RC, F1] = find_prf(z(1), z(2), z(3));
                %res2{mlix, ttix} = [PR, RC, F1, tpfpfnfn1];
            end
        end
        
        res2{xix, ntix} = [PR, RC, F1, tpfpfnfn1];
    end
end
   
loso_gxmin = res2;
save('loso_gxmin','loso_gxmin');