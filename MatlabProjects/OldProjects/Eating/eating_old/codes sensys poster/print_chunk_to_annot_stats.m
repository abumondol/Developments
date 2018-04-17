load('res');

total_chunk = 0;
total_annot = 0;
total_annot_captured = 0;
total_chunk_duplicate = 0;

sub_count = length(res);
for sub = 1:sub_count
    sess_count = length(res(sub).session);
    for sess = 1:sess_count
        stat = res(sub).session(sess).chunk_annot_stat;
        fprintf('Sub %d, Sess %d: %d %d; %d, %d\n', ...
            sub, sess, stat.chunk_count, stat.annot_count, stat.annot_count_captured, stat.chunk_count_has_duplicate_annot);
        
        total_chunk = total_chunk + stat.chunk_count;
        total_annot = total_annot + stat.annot_count;        
        total_annot_captured = total_annot_captured + stat.annot_count_captured;
        total_chunk_duplicate = total_chunk_duplicate + stat.chunk_count_has_duplicate_annot; 
    end
end
    
fprintf('%d %d; %d, %d\n', ...
            total_chunk, total_annot, total_annot_captured, total_chunk_duplicate);

TP = total_annot_captured;
FP = total_chunk - total_annot_captured;
FN = total_annot - total_annot_captured;

P = 100*TP / (TP + FP);
R = 100*TP / (TP + FN);
F1 = 2*P*R/(P+R);
fprintf('Precision: %.2f, Recall: %.2f, F-Score: %.2f\n', P, R, F1);
