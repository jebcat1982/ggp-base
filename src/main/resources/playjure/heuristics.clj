(ns playjure.heuristics)


(defn count-moves [state-machine current-state role]
  (count (.getLegalMoves state-machine current-state role)))

(defn average [& nums]
  (int (/ (apply + nums)
          (count nums))))


(defn static [state-machine current-state role]
  1)

(defn inverse-mobility [state-machine current-state role]
  (int (/ 50 (count-moves state-machine current-state role))))

(defn mobility [state-machine current-state role]
  (- 51 (inverse-mobility state-machine current-state role)))


(defn mix [& heuristics]
  (fn [& args]
    (apply average
           (map #(apply % args)
                heuristics))))
