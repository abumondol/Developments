for ic = 1:5
    for s = 1:10
        fprintf('%d, %d\n', ic, s);
        train_cells = [];
        all_ca =[];
        for i = 1:10
            if i==s 
                continue
            end

            ca = cellassign(ic).subject(s).hw_ca;
            ai = data(s).annot_indices;
            annot_count = length(ai);

            for j = 1:annot_count
                a = ai(j, 1);
                b = ai(j, 2);
                c = ca(a:b);
                c = unique(c);
                all_ca = [all_ca; c];
            end

        end


        cell_count = length(ico(ic).vertices);
        cf = zeros(cell_count, 1);
        for j = 1:length(all_ca)
            ix = all_ca(j);
            cf(ix) = cf(ix)+1;
        end

        all_nb = ico(ic).neighbors;
        for j = 1:cell_count
            if cf(j) == 0 
                nb = all_nb{j};
                a = sum(cf(nb));
                if a>0
                    cf(j) = 0.5;
                end
            end
        end
        
        gen_data(s).ico(ic).train_cell_freq = cf;

    end
end

save('gen_data', 'gen_data');