load('..\oppdata');
load('base_res');
load('our_res');

alpha_vals = 1:0.25:4;
window_size = 30;
step_size = 15;

allres = cell(5, length(alpha_vals));
for k=1:5
    for aix = 1:length(alpha_vals)        
        fprintf('%d, %d\n', k, aix);
        tfn_base = [0 0 0 0]; 
        tfn_ours1 = [0 0 0 0]; 
        tfn_ours2 = [0 0 0 0]; 
        
        newres1 = cell(4,5);
        newres2 = cell(4,5);
        for subj=1:4
            for sess = 1:5
                mask = our_res{k}{subj, sess}{aix}.covered;
                nres1 = [];                
                YPred = base_res{subj, sess}.YPred;
                YPred1 = YPred.*mask;
                nres1.YPred = YPred1;
                                
                nres2 = [];
                YPred2 = our_res{k}{subj, sess}{aix}.YPred;
                YPred2 = YPred2.*mask;
                nres2.YPred = YPred2;
                
                newres1{subj, sess} = nres1;
                newres2{subj, sess} = nres2;
                
                YTest = base_res{subj, sess}.YTest;
                
                
                [TP, FP, FN, TN] = find_tpfpfntn(YTest, YPred);
                tfn_base = tfn_base + [TP, FP, FN, TN]; 
                
                [TP, FP, FN, TN] = find_tpfpfntn(YTest, YPred1);
                tfn_ours1 = tfn_ours1 + [TP, FP, FN, TN]; 
                
                [TP, FP, FN, TN] = find_tpfpfntn(YTest, YPred2);
                tfn_ours2 = tfn_ours2 + [TP, FP, FN, TN];
                
            end
        end
        
        res = [];
        res.ward_res_base = get_ward_res(oppdata, base_res, window_size, step_size);
        res.ward_res_ours1 = get_ward_res(oppdata, newres1, window_size, step_size);
        res.ward_res_ours2 = get_ward_res(oppdata, newres2, window_size, step_size);
        
        res.tfn_base = tfn_base; 
        res.tfn_ours1 = tfn_ours1; 
        res.tfn_ours2 = tfn_ours2; 
        
        [PR, RC, F1] = find_prf(tfn_base(1), tfn_base(2), tfn_base(3));
        res.prf_base = [PR, RC, F1];
        
        [PR, RC, F1] = find_prf(tfn_ours1(1), tfn_ours1(2), tfn_ours1(3));
        res.prf_ours1 = [PR, RC, F1];
        
        [PR, RC, F1] = find_prf(tfn_ours2(1), tfn_ours2(2), tfn_ours2(3));
        res.prf_ours2 = [PR, RC, F1];
        allres{k, aix} = res;
    end
end
save('allres','allres');