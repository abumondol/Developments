function freq = get_train_cell_freq(ico, ico_number, nb_num, gen_data, sub_id)
    ic = ico_number;    
    
    ws = [];
    for i =1:10
        if i==sub_id
            continue
        end
        w = gen_data(i).windows_hw;
        %w = w(w(:, end)==1, ic);
        %ws = [ws;w];
        x = [];
        for j=1:length(w)-1
            if w(j, end) == 1
               x = [x; w(j, ic)];
               if w(j+1, end) == 0
                    x = unique(x);
                    ws = [ws;x]; 
               end
            end
        end

        
    end  
    
    cell_count = length(ico(ic).vertices);
    freq = zeros(cell_count, 1);
    for i = 1: length(ws)        
        freq(ws(i)) = freq(ws(i))+1;
    end    
    
    if nb_num == 0
        return
    end

    if nb_num<0        
        freq(freq<=abs(nb_num)) = 0;
        return
    end
    
    all_nb = ico(ic).neighbors;
    for i=1:nb_num
        for j = 1:cell_count
            if freq(j) == 0 
                nb = all_nb{j};
                a = sum(freq(nb));
                if a>0
                    freq(j) = 1;
                end
            end
        end
    end    

end   